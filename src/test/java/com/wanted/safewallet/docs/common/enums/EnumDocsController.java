package com.wanted.safewallet.docs.common.enums;

import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.enums.FinanceStatus;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import com.wanted.safewallet.global.enums.EnumType;
import java.util.Arrays;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enums")
public class EnumDocsController {

    @GetMapping
    public EnumDocs getEnums() {
        Map<String, String> categoryType = getEnumMap(CategoryType.values());
        Map<String, String> statsCriteria = getEnumMap(StatsCriteria.values());
        Map<String, String> financeStatus = getEnumMap(FinanceStatus.values());
        return EnumDocs.builder()
            .categoryType(categoryType)
            .statsCriteria(statsCriteria)
            .financeStatus(financeStatus)
            .build();
    }

    private Map<String, String> getEnumMap(EnumType[] enumTypes) {
        return Arrays.stream(enumTypes).collect(toMap(EnumType::getName, EnumType::getDescription));
    }
}
