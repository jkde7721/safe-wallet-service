package com.wanted.safewallet.domain.expenditure.web.controller;

import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/expenditures")
@RestController
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    @Value("${temporary.userId}")
    private String userId; //security 구현 전 임시 사용자 ID

    @CommonResponseContent(status = HttpStatus.CREATED)
    @PostMapping
    public ExpenditureCreateResponseDto createExpenditure(@RequestBody @Valid ExpenditureCreateRequestDto requestDto) {
        return expenditureService.createExpenditure(userId, requestDto);
    }

    @PutMapping("/{expenditureId}")
    public void updateExpenditure(@PathVariable Long expenditureId,
        @RequestBody @Valid ExpenditureUpdateRequestDto requestDto) {
        expenditureService.updateExpenditure(userId, expenditureId, requestDto);
    }

    @DeleteMapping("/{expenditureId}")
    public void deleteExpenditure(@PathVariable Long expenditureId) {
        expenditureService.deleteExpenditure(userId, expenditureId);
    }
}
