package com.wanted.safewallet.domain.category.persistence.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wanted.safewallet.global.enums.EnumType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CategoryType implements EnumType {

    FOOD("식비"), TRAFFIC("교통"), RESIDENCE("주거"),
    CLOTHING("패션/쇼핑"), LEISURE("문화/여가"), ETC("기타");

    private final String description;

    @JsonCreator
    public static CategoryType deserialize(String value) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.name().equals(value.toUpperCase())) {
                return categoryType;
            }
        }
        return FOOD;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
