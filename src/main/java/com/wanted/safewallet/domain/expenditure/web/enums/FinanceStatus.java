package com.wanted.safewallet.domain.expenditure.web.enums;

import com.wanted.safewallet.global.enums.EnumType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FinanceStatus implements EnumType {

    EXCELLENT("절약하여 소비한 경우"), GOOD("예산에 맞게 소비한 경우"),
    WARN("예산 초과 위험이 있는 경우"), BAD("현재까지의 지출이 예산을 초과한 경우");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
