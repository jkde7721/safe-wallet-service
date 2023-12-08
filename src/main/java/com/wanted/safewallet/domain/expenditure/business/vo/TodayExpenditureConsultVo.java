package com.wanted.safewallet.domain.expenditure.business.vo;

import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;

public record TodayExpenditureConsultVo(Long todayTotalAmount, FinanceStatus financeStatus) {
}
