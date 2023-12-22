package com.wanted.safewallet.domain.budget.business.mapper;

import com.wanted.safewallet.domain.budget.business.dto.BudgetUpdateDto;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequest;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse.BudgetConsultOfCategoryResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse.BudgetOfCategoryResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponse;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public List<Budget> toEntityList(String userId, BudgetSetUpRequest request) {
        YearMonth budgetYearMonth = request.getBudgetYearMonth();
        return request.getBudgetList().stream()
            .map(budgetOfCategory -> Budget.builder()
                .user(User.builder().id(userId).build())
                .category(Category.builder().id(budgetOfCategory.getCategoryId()).type(budgetOfCategory.getType()).build())
                .budgetYearMonth(budgetYearMonth).amount(budgetOfCategory.getAmount()).build())
            .toList();
    }

    public BudgetUpdateDto toDto(BudgetUpdateRequest request) {
        return BudgetUpdateDto.builder()
            .budgetYearMonth(request.getBudgetYearMonth())
            .categoryId(request.getCategoryId())
            .type(request.getType())
            .amount(request.getAmount()).build();
    }

    public BudgetSetUpResponse toResponse(List<Budget> budgetList) {
        List<BudgetOfCategoryResponse> budgetOfCategoryList = budgetList.stream()
            .map(budget -> new BudgetOfCategoryResponse(budget.getId(), budget.getCategory().getId(),
                budget.getCategory().getType(), budget.getAmount()))
            .toList();
        return new BudgetSetUpResponse(budgetOfCategoryList);
    }

    public BudgetUpdateResponse toResponse(Budget budget) {
        return BudgetUpdateResponse.builder()
            .budgetId(budget.getId())
            .budgetYearMonth(budget.getBudgetYearMonth())
            .categoryId(budget.getCategory().getId())
            .type(budget.getCategory().getType())
            .amount(budget.getAmount()).build();
    }

    public BudgetConsultResponse toResponse(Map<Category, Long> budgetAmountByCategory) {
        List<BudgetConsultOfCategoryResponse> budgetConsultList = budgetAmountByCategory.keySet()
            .stream().sorted(Comparator.comparing(Category::getId))
            .map(category -> new BudgetConsultOfCategoryResponse(category.getId(), category.getType(),
                budgetAmountByCategory.get(category)))
            .toList();
        return new BudgetConsultResponse(budgetConsultList);
    }
}
