package com.wanted.safewallet.domain.budget.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_BUDGET;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.persistence.repository.BudgetRepository;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest.BudgetOfCategoryRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequest;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponse;
import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BudgetService {

    private final BudgetMapper budgetMapper;
    private final CategoryService categoryService;
    private final BudgetRepository budgetRepository;

    @Transactional
    public BudgetSetUpResponse setUpBudget(String userId, BudgetSetUpRequest request) {
        validateRequest(userId, request);
        List<Budget> budgetList = budgetMapper.toEntityList(userId, request);
        budgetRepository.saveAll(budgetList);
        return budgetMapper.toDto(budgetList);
    }

    @Transactional
    public BudgetUpdateResponse updateBudget(String userId, Long budgetId,
        BudgetUpdateRequest request) {
        validateRequest(request);
        Budget budget = getValidBudget(userId, budgetId);

        Budget anotherBudget = budgetRepository.findByUserAndCategoryAndBudgetYearMonthFetch(
            userId, request.getCategoryId(), request.getBudgetYearMonth())
            .orElse(budget);
        if (Objects.equals(anotherBudget.getId(), budgetId)) {
            anotherBudget.update(request.getCategoryId(), request.getType(),
                request.getAmount(), request.getBudgetYearMonth());
        }
        else {
            budgetRepository.deleteById(budgetId);
            anotherBudget.addAmount(request.getAmount());
        }
        return budgetMapper.toDto(anotherBudget);
    }

    //TODO: Redis Cache 적용
    public BudgetConsultResponse consultBudget(String userId, Long totalAmountForConsult) {
        Map<Category, Long> prevBudgetAmountByCategory = budgetRepository.existsByUser(userId) ?
            getBudgetAmountByCategory(userId) : getBudgetAmountByCategory();
        Map<Category, Long> consultedBudgetAmountByCategory = consultBudgetAmount(totalAmountForConsult, prevBudgetAmountByCategory);
        return budgetMapper.toDto(consultedBudgetAmountByCategory);
    }

    public Map<Category, Long> getBudgetAmountByCategory(String userId, YearMonth budgetYearMonth) {
        return budgetRepository.findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(userId, budgetYearMonth)
            .toMapByCategory();
    }

    public Map<Category, Long> getBudgetAmountByCategory(String userId) {
        return budgetRepository.findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(userId, null)
            .toMapByCategory();
    }

    public Map<Category, Long> getBudgetAmountByCategory() {
        return budgetRepository.findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(null, null)
            .toMapByCategory();
    }

    public Budget getValidBudget(String userId, Long budgetId) {
        Budget budget = getBudget(budgetId);
        if (!Objects.equals(budget.getUser().getId(), userId)) {
            throw new BusinessException(FORBIDDEN_BUDGET);
        }
        return budget;
    }

    public Budget getBudget(Long budgetId) {
        return budgetRepository.findById(budgetId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_BUDGET));
    }

    private Map<Category, Long> consultBudgetAmount(Long totalAmountForConsult,
        Map<Category, Long> prevBudgetAmountByCategory) {
        long prevTotalAmount = prevBudgetAmountByCategory.values().stream().mapToLong(Long::longValue).sum();
        Map<Category, Long> budgetAmountByCategory = prevBudgetAmountByCategory.keySet().stream()
            .collect(toMap(identity(), category -> calculateBudgetAmount(category.getType(),
                prevBudgetAmountByCategory.get(category), prevTotalAmount, totalAmountForConsult)));
        consultBudgetAmountOfEtcCategory(totalAmountForConsult, budgetAmountByCategory);
        return budgetAmountByCategory;
    }

    private Long calculateBudgetAmount(CategoryType type, Long prevAmountOfCategory,
        Long prevTotalAmount, Long totalAmountForConsult) {
        if (type == CategoryType.ETC || (double) prevAmountOfCategory / prevTotalAmount <= 0.10) {
            return 0L;
        }
        double ratio = (double) prevAmountOfCategory / prevTotalAmount;
        return ((long) (totalAmountForConsult * ratio / 100)) * 100; //100원 아래 버림
    }

    private void consultBudgetAmountOfEtcCategory(Long totalAmountForConsult,
        Map<Category, Long> budgetAmountByCategory) {
        long remainedAmount = totalAmountForConsult - budgetAmountByCategory.values().stream()
            .mapToLong(Long::longValue).sum();
        Category etcCategory = Category.builder().type(CategoryType.ETC).build();
        budgetAmountByCategory.replace(etcCategory, remainedAmount);
    }

    private void validateRequest(String userId, BudgetSetUpRequest request) {
        List<CategoryValidationDto> categoryValidationDtoList = request.getBudgetList().stream()
            .map(b -> new CategoryValidationDto(b.getCategoryId(), b.getType())).toList();
        List<Long> categoryIds = request.getBudgetList().stream().map(
            BudgetOfCategoryRequest::getCategoryId).toList();

        categoryService.validateCategory(categoryValidationDtoList);
        if (budgetRepository.existsByUserAndBudgetYearMonthAndCategories(
            userId, request.getBudgetYearMonth(), categoryIds)) {
            throw new BusinessException(ALREADY_EXISTS_BUDGET);
        }
    }

    private void validateRequest(BudgetUpdateRequest request) {
        CategoryValidationDto categoryValidationDto = new CategoryValidationDto(
            request.getCategoryId(), request.getType());
        categoryService.validateCategory(categoryValidationDto);
    }
}
