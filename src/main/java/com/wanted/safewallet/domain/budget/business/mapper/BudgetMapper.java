package com.wanted.safewallet.domain.budget.business.mapper;

import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponseDto.BudgetConsultOfCategoryResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto.BudgetOfCategory;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponseDto;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public List<Budget> toEntityList(String userId, BudgetSetUpRequestDto requestDto) {
        YearMonth budgetYearMonth = requestDto.getBudgetYearMonth();
        return requestDto.getBudgetList().stream()
            .map(budgetOfCategory -> Budget.builder()
                .user(User.builder().id(userId).build())
                .category(Category.builder().id(budgetOfCategory.getCategoryId()).type(budgetOfCategory.getType()).build())
                .budgetYearMonth(budgetYearMonth).amount(budgetOfCategory.getAmount()).build())
            .toList();
    }

    public BudgetSetUpResponseDto toDto(List<Budget> budgetList) {
        List<BudgetOfCategory> budgetOfCategoryList = budgetList.stream()
            .map(budget -> new BudgetOfCategory(budget.getId(), budget.getCategory().getId(),
                budget.getCategory().getType(), budget.getAmount()))
            .toList();
        return new BudgetSetUpResponseDto(budgetOfCategoryList);
    }

    public BudgetUpdateResponseDto toDto(Budget budget) {
        return BudgetUpdateResponseDto.builder()
            .budgetId(budget.getId())
            .budgetYearMonth(budget.getBudgetYearMonth())
            .categoryId(budget.getCategory().getId())
            .type(budget.getCategory().getType())
            .amount(budget.getAmount()).build();
    }

    public BudgetConsultResponseDto toDto(Map<Category, Long> budgetAmountByCategory) {
        List<BudgetConsultOfCategoryResponseDto> budgetConsultList = budgetAmountByCategory.keySet()
            .stream().sorted(Comparator.comparing(Category::getId))
            .map(category -> new BudgetConsultOfCategoryResponseDto(category.getId(), category.getType(),
                budgetAmountByCategory.get(category)))
            .toList();
        return new BudgetConsultResponseDto(budgetConsultList);
    }
}
