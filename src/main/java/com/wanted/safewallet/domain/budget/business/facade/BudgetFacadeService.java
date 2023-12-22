package com.wanted.safewallet.domain.budget.business.facade;

import com.wanted.safewallet.domain.budget.business.dto.BudgetUpdateDto;
import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest.BudgetOfCategoryRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequest;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponse;
import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BudgetFacadeService {

    private final BudgetMapper budgetMapper;
    private final BudgetService budgetService;
    private final CategoryService categoryService;

    @Transactional
    public BudgetSetUpResponse setUpBudget(String userId, BudgetSetUpRequest request) {
        validateRequest(userId, request);
        List<Budget> budgetList = budgetMapper.toEntityList(userId, request);
        List<Budget> savedBudgetList = budgetService.saveBudgetList(budgetList);
        return budgetMapper.toResponse(savedBudgetList);
    }

    @Transactional
    public BudgetUpdateResponse updateBudget(String userId, Long budgetId, BudgetUpdateRequest request) {
        validateRequest(request);
        BudgetUpdateDto updateDto = budgetMapper.toDto(request);
        Budget updatedBudget = budgetService.updateBudget(userId, budgetId, updateDto);
        return budgetMapper.toResponse(updatedBudget);
    }

    public BudgetConsultResponse consultBudget(String userId, Long totalAmount) {
        Map<Category, Long> consultedBudgetAmountByCategory = budgetService.consultBudget(userId, totalAmount);
        return budgetMapper.toResponse(consultedBudgetAmountByCategory);
    }

    private void validateRequest(String userId, BudgetSetUpRequest request) {
        List<CategoryValidationDto> categoryValidationDtoList = request.getBudgetList().stream()
            .map(b -> new CategoryValidationDto(b.getCategoryId(), b.getType())).toList();
        List<Long> categoryIds = request.getBudgetList().stream().map(
            BudgetOfCategoryRequest::getCategoryId).toList();

        categoryService.validateCategory(categoryValidationDtoList);
        budgetService.checkForDuplicatedBudget(userId, request.getBudgetYearMonth(), categoryIds);
    }

    private void validateRequest(BudgetUpdateRequest request) {
        CategoryValidationDto categoryValidationDto = new CategoryValidationDto(
            request.getCategoryId(), request.getType());
        categoryService.validateCategory(categoryValidationDto);
    }
}
