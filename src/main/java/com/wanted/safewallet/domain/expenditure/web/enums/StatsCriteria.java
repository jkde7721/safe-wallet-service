package com.wanted.safewallet.domain.expenditure.web.enums;

import com.wanted.safewallet.global.enums.EnumType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatsCriteria implements EnumType {

    LAST_YEAR("지난 년도"), LAST_MONTH("지난 달"), LAST_WEEK("지난 주");

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
