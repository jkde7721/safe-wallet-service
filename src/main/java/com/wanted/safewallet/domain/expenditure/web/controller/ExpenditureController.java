package com.wanted.safewallet.domain.expenditure.web.controller;

import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureConsultService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureDailyStatsService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse;
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
    private final ExpenditureDailyStatsService expenditureDailyStatsService;

    @GetMapping("/{expenditureId}")
    public ExpenditureDetailsResponse getExpenditureDetails(@PathVariable Long expenditureId,
        @CurrentUserId String userId) {
        return expenditureService.getExpenditureDetails(userId, expenditureId);
    }

    @GetMapping
    public ExpenditureSearchResponse searchExpenditure(@CurrentUserId String userId,
        @ModelAttribute @Valid ExpenditureSearchCond searchCond, Pageable pageable) {
        return expenditureService.searchExpenditure(userId, searchCond, pageable);
    }

    @GetMapping("/excepts")
    public ExpenditureSearchExceptsResponse searchExpenditureExcepts(@CurrentUserId String userId,
        @ModelAttribute @Valid ExpenditureSearchCond searchCond) {
        return expenditureService.searchExpenditureExcepts(userId, searchCond);
    }

    @CommonResponseContent(status = HttpStatus.CREATED)
    @PostMapping
    public ExpenditureCreateResponse createExpenditure(@RequestBody @Valid ExpenditureCreateRequest request,
        @CurrentUserId String userId) {
        return expenditureService.createExpenditure(userId, request);
    }

    @PutMapping("/{expenditureId}")
    public void updateExpenditure(@PathVariable Long expenditureId,
        @RequestBody @Valid ExpenditureUpdateRequest request,
        @CurrentUserId String userId) {
        expenditureService.updateExpenditure(userId, expenditureId, request);
    }

    @DeleteMapping("/{expenditureId}")
    public void deleteExpenditure(@PathVariable Long expenditureId, @CurrentUserId String userId) {
        expenditureService.deleteExpenditure(userId, expenditureId);
    }

    @GetMapping("/stats")
    public ExpenditureStatsResponse produceExpenditureStats(@CurrentUserId String userId,
        @RequestParam(defaultValue = "LAST_MONTH") StatsCriteria criteria) {
        return expenditureService.produceExpenditureStats(userId, criteria);
    }

    @GetMapping("/consult")
    public TodayExpenditureConsultResponse consultTodayExpenditure(@CurrentUserId String userId) {
        return expenditureConsultService.consultTodayExpenditure(userId);
    }

    @GetMapping("/daily-stats")
    public YesterdayExpenditureDailyStatsResponse produceYesterdayExpenditureDailyStats(@CurrentUserId String userId) {
        return expenditureDailyStatsService.produceYesterdayExpenditureDailyStats(userId);
    }
}
