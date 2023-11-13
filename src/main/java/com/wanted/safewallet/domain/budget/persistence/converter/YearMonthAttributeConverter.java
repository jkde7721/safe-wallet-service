package com.wanted.safewallet.domain.budget.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(YearMonth yearMonth) {
        if (yearMonth != null) {
            return LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate localDate) {
        if (localDate != null) {
            return YearMonth.of(localDate.getYear(), localDate.getMonth());
        }
        return null;
    }
}
