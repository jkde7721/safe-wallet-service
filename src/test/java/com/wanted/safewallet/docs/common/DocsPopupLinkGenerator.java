package com.wanted.safewallet.docs.common;

import lombok.RequiredArgsConstructor;

public abstract class DocsPopupLinkGenerator {

    public static String generatePopupLink(DocsPopupInfo docsPopupInfo) {
        return String.format("link:%s.html[%s,role=\"popup\"]",
            docsPopupInfo.fileName, docsPopupInfo.description);
    }

    @RequiredArgsConstructor
    public enum DocsPopupInfo {
        CATEGORY_TYPE("category-type", "카테고리 타입"),
        STATS_CRITERIA("stats-criteria", "지출 통계 기준"),
        FINANCE_STATUS("finance-status", "예산 대비 지출에 따른 재정 상태"),
        PAGING_RESPONSE("paging-response", "페이징 응답"),
        PASSWORD_CONSTRAINTS("password-constraints", "비밀번호 제약조건");

        private final String fileName;
        private final String description;
    }
}
