package com.wanted.safewallet.domain.budget.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_BUDGET;

import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.persistence.repository.BudgetRepository;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto.BudgetByCategory;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponseDto;
import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
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
    public BudgetSetUpResponseDto setUpBudget(String userId, BudgetSetUpRequestDto requestDto) {
        validateRequest(userId, requestDto);
        List<Budget> budgetList = budgetMapper.toEntityList(userId, requestDto);
        budgetRepository.saveAll(budgetList);
        return budgetMapper.toDto(budgetList);
    }

    @Transactional
    public BudgetUpdateResponseDto updateBudget(String userId, Long budgetId,
        BudgetUpdateRequestDto requestDto) {
        validateRequest(requestDto);
        Budget budget = getValidBudget(userId, budgetId);

        Budget anotherBudget = budgetRepository.findByCategoryAndBudgetYearMonthFetch(
            requestDto.getCategoryId(), requestDto.getBudgetYearMonth()).orElse(budget);
        if (Objects.equals(anotherBudget.getId(), budgetId)) {
            anotherBudget.update(requestDto.getCategoryId(), requestDto.getType(),
                requestDto.getAmount(), requestDto.getBudgetYearMonth());
        }
        else {
            budgetRepository.deleteById(budgetId);
            anotherBudget.addAmount(requestDto.getAmount());
        }
        return budgetMapper.toDto(anotherBudget);
    }

    public Budget getValidBudget(String userId, Long budgetId) {
        Budget budget = getBudget(budgetId);
        if (Objects.equals(budget.getUser().getId(), userId)) {
            return budget;
        }
        throw new BusinessException(FORBIDDEN_BUDGET);
    }

    public Budget getBudget(Long budgetId) {
        return budgetRepository.findById(budgetId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_BUDGET));
    }

    private void validateRequest(String userId, BudgetSetUpRequestDto requestDto) {
        List<CategoryValidRequestDto> categoryValidDtoList = requestDto.getBudgetList().stream()
            .map(b -> new CategoryValidRequestDto(b.getCategoryId(), b.getType())).toList();
        List<Long> categoryIds = requestDto.getBudgetList().stream().map(
            BudgetByCategory::getCategoryId).toList();

        categoryService.validateCategory(categoryValidDtoList);
        if (budgetRepository.existsByUserIdAndBudgetYearMonthAndInCategories(
            userId, requestDto.getBudgetYearMonth(), categoryIds)) {
            throw new BusinessException(ALREADY_EXISTS_BUDGET);
        }
    }

    private void validateRequest(BudgetUpdateRequestDto requestDto) {
        CategoryValidRequestDto categoryValidDto = new CategoryValidRequestDto(
            requestDto.getCategoryId(), requestDto.getType());
        categoryService.validateCategory(categoryValidDto);
    }
}
