package com.wanted.safewallet.docs.common.response;

import static org.springframework.restdocs.payload.JsonFieldType.VARIES;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

@WithMockCustomUser
@WebMvcTest(CommonResponseController.class)
class CommonResponseControllerTest extends AbstractRestDocsTest {

    @DisplayName("공통 응답 필드 문서화하기 위한 테스트")
    @Test
    void getCommonResponse() throws Exception {
        restDocsMockMvc.perform(get("/commonResponse")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                    responseFields(
                        fieldWithPath("timestamp").description("응답 시간"),
                        fieldWithPath("status").description("응답 상태값"),
                        fieldWithPath("code").description("응답 상태 코드 또는 에러 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").type(VARIES).description("응답 데이터"),
                        fieldWithPath("data.name").ignored(),
                        fieldWithPath("data.age").ignored()
                    )
                )
            );
    }
}
