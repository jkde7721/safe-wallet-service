package com.wanted.safewallet.docs.common.enums;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StringUtils.uncapitalize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WithMockCustomUser
@WebMvcTest(EnumDocsController.class)
class EnumDocsControllerTest extends AbstractRestDocsTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ATTRIBUTE_KEY = "title";

    @DisplayName("Enum 클래스 문서화하기 위한 테스트")
    @Test
    void getEnums() throws Exception {
        ResultActions resultActions = restDocsMockMvc.perform(get("/enums")
            .accept(MediaType.APPLICATION_JSON));
        EnumDocs enumDocs = getData(resultActions.andReturn());

        resultActions.andExpect(status().isOk())
            .andDo(restDocs.document(
                enumFieldsSnippet(enumDocs.getCategoryType(), CategoryType.class.getSimpleName()),
                enumFieldsSnippet(enumDocs.getStatsCriteria(), StatsCriteria.class.getSimpleName()),
                enumFieldsSnippet(enumDocs.getFinanceStatus(), FinanceStatus.class.getSimpleName())));
    }

    private static EnumDocs getData(MvcResult mvcResult) throws IOException {
        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(content, EnumDocs.class);
    }

    private static EnumFieldsSnippet enumFieldsSnippet(Map<String, String> enumValues, String enumTypeName) {
        return new EnumFieldsSnippet(convertFieldDescriptors(enumValues),
            attributes(key(ATTRIBUTE_KEY).value(enumTypeName)), true,
            beneathPath(uncapitalize(enumTypeName)).withSubsectionId(uncapitalize(enumTypeName)));
    }

    private static List<FieldDescriptor> convertFieldDescriptors(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
            .map(e -> fieldWithPath(e.getKey()).description(e.getValue()))
            .toList();
    }
}
