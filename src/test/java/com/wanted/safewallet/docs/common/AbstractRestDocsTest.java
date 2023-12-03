package com.wanted.safewallet.docs.common;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.wanted.safewallet.docs.config.RestDocsConfig;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.config.BeanIds;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractRestDocsTest {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc restDocsMockMvc;

    @Autowired
    protected MockMvc authRestDocsMockMvc;

    @BeforeEach
    void init(final WebApplicationContext context,
        final RestDocumentationContextProvider provider) throws ServletException {
        this.restDocsMockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(provider))
            .alwaysDo(print())
            .alwaysDo(restDocs)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();

        //Auth 도메인 MockMvc 테스트에 Spring Security Filter 적용
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        delegatingFilterProxy.init(new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));
        this.authRestDocsMockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(provider))
            .alwaysDo(print())
            .alwaysDo(restDocs)
            .addFilters(new CharacterEncodingFilter("UTF-8", true), delegatingFilterProxy)
            .build();
    }
}
