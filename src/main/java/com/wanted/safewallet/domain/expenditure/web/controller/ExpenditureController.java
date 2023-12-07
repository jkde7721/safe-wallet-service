package com.wanted.safewallet.domain.expenditure.web.controller;

import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureConsultService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/expenditures")
@RestController
public class ExpenditureController {

    private final ExpenditureService expenditureService;
    private final ExpenditureConsultService expenditureConsultService;

    @GetMapping("/{expenditureId}")
    public ExpenditureDetailsResponseDto getExpenditureDetails(@PathVariable Long expenditureId,
        @CurrentUserId String userId) {
        return expenditureService.getExpenditureDetails(userId, expenditureId);
    }

    @GetMapping
    public ExpenditureSearchResponseDto searchExpenditure(@CurrentUserId String userId,
        @ModelAttribute @Valid ExpenditureSearchCond searchCond, Pageable pageable) {
        return expenditureService.searchExpenditure(userId, searchCond, pageable);
    }

    @GetMapping("/excepts")
    public ExpenditureSearchExceptsResponseDto searchExpenditureExcepts(@CurrentUserId String userId,
        @ModelAttribute @Valid ExpenditureSearchCond searchCond) {
        return expenditureService.searchExpenditureExcepts(userId, searchCond);
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

    @GetMapping("/stats")
    public ExpenditureStatsResponseDto produceExpenditureStats(@CurrentUserId String userId,
        @RequestParam(defaultValue = "LAST_MONTH") StatsCriteria criteria) {
        return expenditureService.produceExpenditureStats(userId, criteria);
    }

    @GetMapping("/consult")
    public TodayExpenditureConsultResponseDto consultTodayExpenditure(@CurrentUserId String userId) {
        return expenditureConsultService.consultTodayExpenditure(userId);
    }
}
