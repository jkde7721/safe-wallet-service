package com.wanted.safewallet.docs.common.enums;

import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
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
        return EnumDocs.builder()
            .categoryType(categoryType)
            .build();
    }

    private Map<String, String> getEnumMap(EnumType[] enumTypes) {
        return Arrays.stream(enumTypes).collect(toMap(EnumType::getName, EnumType::getDescription));
    }
}
