package com.wanted.safewallet.docs.common.enums;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumDocs {

    //문서화 대상 Enum 타입명
    Map<String, String> categoryType;
    Map<String, String> statsCriteria;
    Map<String, String> financeStatus;
}
