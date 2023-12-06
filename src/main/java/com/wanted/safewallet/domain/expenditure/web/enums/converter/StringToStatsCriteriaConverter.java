package com.wanted.safewallet.domain.expenditure.web.enums.converter;

import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_MONTH;

import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStatsCriteriaConverter implements Converter<String, StatsCriteria> {

    @Override
    public StatsCriteria convert(String source) {
        for (StatsCriteria statsCriteria : StatsCriteria.values()) {
            if (statsCriteria.name().equals(source.toUpperCase())) {
                return statsCriteria;
            }
        }
        return LAST_MONTH;
    }
}
