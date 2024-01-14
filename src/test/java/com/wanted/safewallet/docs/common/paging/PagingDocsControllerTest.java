package com.wanted.safewallet.docs.common.paging;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.global.dto.response.aop.PageStore;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Import(PageStore.class)
@WithMockCustomUser
@WebMvcTest(PagingDocsController.class)
class PagingDocsControllerTest extends AbstractRestDocsTest {

    @DisplayName("페이징 응답 문서화하기 위한 테스트")
    @Test
    void getPaging() throws Exception {
        restDocsMockMvc.perform(get("/paging")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("pageSize").description("한 페이지 내 요소 최대 개수"),
                    fieldWithPath("numberOfElements").description("현재 페이지 내 요소 개수"),
                    fieldWithPath("totalPages").description("전체 페이지 개수"),
                    fieldWithPath("totalElements").description("전체 요소 개수"),
                    fieldWithPath("first").description("첫 페이지 여부"),
                    fieldWithPath("last").description("마지막 페이지 여부"),
                    fieldWithPath("empty").description("현재 페이지가 빈 페이지인지 여부"))));
    }
}
