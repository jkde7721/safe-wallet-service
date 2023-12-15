package com.wanted.safewallet.domain.expenditure.business.dto;

import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;

public record TodayExpenditureConsultDto(Long amount, FinanceStatus financeStatus) {
}
