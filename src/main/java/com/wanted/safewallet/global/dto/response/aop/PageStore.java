package com.wanted.safewallet.global.dto.response.aop;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageStore {

    private final ThreadLocal<Page<?>> page = new ThreadLocal<>();

    public void setPage(Page<?> page) {
        this.page.set(page);
    }

    public Page<?> getPage() {
        Page<?> storedPage = this.page.get();
        this.page.remove();
        return storedPage;
    }
}
