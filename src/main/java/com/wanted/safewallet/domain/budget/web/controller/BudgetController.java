package com.wanted.safewallet.domain.budget.web.controller;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/budgets")
@RestController
public class BudgetController {

    private final BudgetService budgetService;

    @Value("${temporary.userId}")
    private String userId; //security 구현 전 임시 사용자 ID

    @PostMapping
    public ResponseEntity<BudgetSetUpResponseDto> setUpBudget(@RequestBody @Valid BudgetSetUpRequestDto requestDto) {
        BudgetSetUpResponseDto responseDto = budgetService.setUpBudget(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
