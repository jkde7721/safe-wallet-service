package com.wanted.safewallet.domain.budget.business.mapper;

import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponseDto.BudgetConsultByCategoryResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto.BudgetByCategory;
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
        return requestDto.getBudgetList().stream().map(b -> Budget.builder()
                .user(User.builder().id(userId).build())
                .category(Category.builder().id(b.getCategoryId()).type(b.getType()).build())
                .budgetYearMonth(budgetYearMonth).amount(b.getAmount()).build())
            .toList();
    }

    public BudgetSetUpResponseDto toDto(List<Budget> budgetList) {
        List<BudgetByCategory> budgetListByCategory = budgetList.stream()
            .map(b -> new BudgetByCategory(b.getId(), b.getCategory().getId(),
                b.getCategory().getType(), b.getAmount()))
            .toList();
        return new BudgetSetUpResponseDto(budgetListByCategory);
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
        List<BudgetConsultByCategoryResponseDto> budgetConsultList = budgetAmountByCategory.keySet()
            .stream().sorted(Comparator.comparing(Category::getId))
            .map(category -> new BudgetConsultByCategoryResponseDto(category.getId(), category.getType(),
                budgetAmountByCategory.get(category)))
            .toList();
        return new BudgetConsultResponseDto(budgetConsultList);
    }
}
