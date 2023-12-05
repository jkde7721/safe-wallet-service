package com.wanted.safewallet.domain.budget.web.controller;

import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo;
import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto.BudgetByCategory;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponseDto;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import java.time.YearMonth;
import java.util.List;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WithMockCustomUser
@WebMvcTest(BudgetController.class)
class BudgetControllerTest extends AbstractRestDocsTest {

    @MockBean
    BudgetService budgetService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("월별 예산 설정 컨트롤러 테스트 : 성공")
    @Test
    void setUpBudget() throws Exception {
        //given
        BudgetSetUpResponseDto responseDto = new BudgetSetUpResponseDto(List.of(
            new BudgetByCategory(1L, 1L, CategoryType.FOOD, 10000L),
            new BudgetByCategory(2L, 2L, CategoryType.TRAFFIC, 5000L)));
        given(budgetService.setUpBudget(anyString(), any(BudgetSetUpRequestDto.class))).willReturn(responseDto);

        //when, then
        BudgetSetUpRequestDto requestDto = new BudgetSetUpRequestDto(YearMonth.now(),
            List.of(new BudgetSetUpRequestDto.BudgetByCategory(1L, CategoryType.FOOD, 10000L),
                new BudgetSetUpRequestDto.BudgetByCategory(2L, CategoryType.TRAFFIC, 5000L)));
        restDocsMockMvc.perform(post("/api/budgets")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.budgetList", hasSize(2)))
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("budgetYearMonth").description("예산 설정 년월")
                        .attributes(key("constraints").value("오늘 날짜 이후만 가능"))
                        .attributes(key("formats").value("yyyy-M 또는 yyyy/M 또는 yyyy.M")),
                    fieldWithPath("budgetList").description("예산 설정 목록")
                        .attributes(key("constraints").value("같은 날짜, 같은 카테고리의 기존 예산 설정 내역이 있다면 설정 불가")),
                    fieldWithPath("budgetList[].categoryId").description("카테고리 id"),
                    fieldWithPath("budgetList[].type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("budgetList[].amount").description("해당 카테고리의 예산 설정 금액")),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("budgetList").description("생성된 예산 목록"),
                    fieldWithPath("budgetList[].budgetId").description("생성된 예산 id"),
                    fieldWithPath("budgetList[].categoryId").description("생성된 예산 카테고리 id"),
                    fieldWithPath("budgetList[].type").description("생성된 예산 카테고리 타입"),
                    fieldWithPath("budgetList[].amount").description("생성된 예산 금액"))));
        then(budgetService).should(times(1))
            .setUpBudget(anyString(), any(BudgetSetUpRequestDto.class));
    }

    @DisplayName("월별 예산 설정 컨트롤러 테스트 : 실패")
    @Test
    void setUpBudget_validation_fail() throws Exception {
        //given
        BudgetSetUpRequestDto requestDto = new BudgetSetUpRequestDto(
            YearMonth.now().minusMonths(1), List.of());

        //when, then
        mockMvc.perform(post("/api/budgets")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("budgetYearMonth"), containsString("budgetList"))))
            .andDo(print());
        then(budgetService).should(times(0))
            .setUpBudget(anyString(), any(BudgetSetUpRequestDto.class));
    }

    @DisplayName("월별 예산 수정 컨트롤러 테스트 : 성공")
    @Test
    void updateBudget() throws Exception {
        //given
        Long budgetId = 1L;
        BudgetUpdateRequestDto requestDto = new BudgetUpdateRequestDto(YearMonth.now(),
            2L, CategoryType.TRAFFIC, 20000L);
        BudgetUpdateResponseDto responseDto = new BudgetUpdateResponseDto(budgetId,
            requestDto.getBudgetYearMonth(), requestDto.getCategoryId(), requestDto.getType(), requestDto.getAmount());
        given(budgetService.updateBudget(anyString(), anyLong(), any(BudgetUpdateRequestDto.class)))
            .willReturn(responseDto);

        //when, then
        restDocsMockMvc.perform(put("/api/budgets/" + budgetId)
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("budgetYearMonth").description("예산 내역 수정 년월")
                        .attributes(key("formats").value("yyyy-M 또는 yyyy/M 또는 yyyy.M")),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("amount").description("예산 내역 수정 금액")
                        .attributes(key("constraints").value("0원 이상 100,000,000원 이하"))),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("budgetId").description("수정된 예산 내역의 id"),
                    fieldWithPath("budgetYearMonth").description("예산 내역 년월"),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("amount").description("예산 내역 금액"))
            ));
    }

    @DisplayName("월별 예산 수정 컨트롤러 테스트 : 실패")
    @Test
    void updateBudget_validation_fail() throws Exception {
        //given
        Long budgetId = 1L;
        BudgetUpdateRequestDto requestDto = new BudgetUpdateRequestDto(null,
            2L, CategoryType.TRAFFIC, -1L);

        //when, then
        mockMvc.perform(put("/api/budgets/" + budgetId)
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("budgetYearMonth: 월별 예산 수정값이 널일 수 없습니다."),
                containsString("amount: 월별 예산 금액은 0원 이상이어야 합니다."))))
            .andDo(print());
    }
}
