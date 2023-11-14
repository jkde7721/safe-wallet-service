package com.wanted.safewallet.domain.budget.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_BUDGET;

import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.persistence.repository.BudgetRepository;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto.BudgetByCategory;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
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
}
