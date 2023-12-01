package com.wanted.safewallet.docs.common;

import lombok.RequiredArgsConstructor;

public abstract class DocsPopupLinkGenerator {

    public static String generatePopupLink(DocsPopupInfo docsPopupInfo) {
        return String.format("link:%s.html[%s,role=\"popup\"]",
            docsPopupInfo.fileName, docsPopupInfo.description);
    }

    @RequiredArgsConstructor
    public enum DocsPopupInfo {
        CATEGORY_TYPE("category-type", "카테고리 타입");

        private final String fileName;
        private final String description;
    }
}
