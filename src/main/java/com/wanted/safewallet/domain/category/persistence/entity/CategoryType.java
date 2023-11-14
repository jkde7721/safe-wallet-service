package com.wanted.safewallet.domain.category.persistence.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryType {

    FOOD, TRAFFIC, RESIDENCE, CLOTHING, LEISURE, ETC;

    @JsonCreator
    public static CategoryType deserialize(String value) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.name().equals(value.toUpperCase())) {
                return categoryType;
            }
        }
        return FOOD;
    }
}
