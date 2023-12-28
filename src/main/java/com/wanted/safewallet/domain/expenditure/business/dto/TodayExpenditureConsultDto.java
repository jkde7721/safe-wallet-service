package com.wanted.safewallet.domain.expenditure.business.dto;

import com.wanted.safewallet.domain.expenditure.business.enums.FinanceStatus;

public record TodayExpenditureConsultDto(Long amount, FinanceStatus financeStatus) {
}
