package com.wanted.safewallet.domain.budget.web.controller;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponseDto;
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

    private final BudgetService budgetService;

    @CommonResponseContent(status = HttpStatus.CREATED)
    @PostMapping
    public BudgetSetUpResponseDto setUpBudget(@RequestBody @Valid BudgetSetUpRequestDto requestDto,
        @CurrentUserId String userId) {
        return budgetService.setUpBudget(userId, requestDto);
    }

    @PutMapping("/{budgetId}")
    public BudgetUpdateResponseDto updateBudget(@PathVariable Long budgetId,
        @RequestBody @Valid BudgetUpdateRequestDto requestDto, @CurrentUserId String userId) {
        return budgetService.updateBudget(userId, budgetId, requestDto);
    }

    @GetMapping("/consult")
    public BudgetConsultResponseDto consultBudget(@CurrentUserId String userId,
        @RequestParam(required = false) @ValidTotalAmountForConsult Long totalAmount) {
        return budgetService.consultBudget(userId, totalAmount);
    }
}
