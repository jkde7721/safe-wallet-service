package com.wanted.safewallet.domain.budget.web.controller;

import com.wanted.safewallet.domain.budget.facade.BudgetFacadeService;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequest;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponse;
import com.wanted.safewallet.domain.budget.web.validation.annotation.ValidTotalAmountForConsult;
import com.wanted.safewallet.global.auth.annotation.CurrentUserId;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
@RestController
public class BudgetController {

    private final BudgetFacadeService budgetFacadeService;

    @CommonResponseContent(status = HttpStatus.CREATED)
    @PostMapping
    public BudgetSetUpResponse setUpBudget(@CurrentUserId String userId,
        @RequestBody @Valid BudgetSetUpRequest request) {
        return budgetFacadeService.setUpBudget(userId, request);
    }

    @PutMapping("/{budgetId}")
    public BudgetUpdateResponse updateBudget(@CurrentUserId String userId, @PathVariable Long budgetId,
        @RequestBody @Valid BudgetUpdateRequest request) {
        return budgetFacadeService.updateBudget(userId, budgetId, request);
    }

    @GetMapping("/consult")
    public BudgetConsultResponse consultBudget(@CurrentUserId String userId,
        @RequestParam(required = false) @ValidTotalAmountForConsult Long totalAmount) {
        return budgetFacadeService.consultBudget(userId, totalAmount);
    }
}
