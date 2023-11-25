package com.wanted.safewallet.domain.expenditure.web.controller;

import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureExceptsResponseDto;
import com.wanted.safewallet.global.auth.annotation.CurrentUserId;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping("/{expenditureId}")
    public ExpenditureDetailsResponseDto getExpenditureDetails(@PathVariable Long expenditureId,
        @CurrentUserId String userId) {
        return expenditureService.getExpenditureDetails(userId, expenditureId);
    }

    @GetMapping
    public ExpenditureListResponseDto getExpenditureList(@CurrentUserId String userId,
        @ModelAttribute @Valid ExpenditureSearchCond searchCond, Pageable pageable) {
        return expenditureService.getExpenditureList(userId, searchCond, pageable);
    }

    @GetMapping("/excepts")
    public ExpenditureExceptsResponseDto getExpenditureExcepts(@CurrentUserId String userId,
        @ModelAttribute @Valid ExpenditureSearchCond searchCond) {
        return expenditureService.getExpenditureExcepts(userId, searchCond);
    }

    @CommonResponseContent(status = HttpStatus.CREATED)
    @PostMapping
    public ExpenditureCreateResponseDto createExpenditure(@RequestBody @Valid ExpenditureCreateRequestDto requestDto,
        @CurrentUserId String userId) {
        return expenditureService.createExpenditure(userId, requestDto);
    }

    @PutMapping("/{expenditureId}")
    public void updateExpenditure(@PathVariable Long expenditureId,
        @RequestBody @Valid ExpenditureUpdateRequestDto requestDto,
        @CurrentUserId String userId) {
        expenditureService.updateExpenditure(userId, expenditureId, requestDto);
    }

    @DeleteMapping("/{expenditureId}")
    public void deleteExpenditure(@PathVariable Long expenditureId, @CurrentUserId String userId) {
        expenditureService.deleteExpenditure(userId, expenditureId);
    }
}
