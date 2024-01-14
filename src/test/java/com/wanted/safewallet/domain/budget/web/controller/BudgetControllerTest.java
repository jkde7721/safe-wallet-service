package com.wanted.safewallet.domain.budget.web.controller;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.CLOTHING;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.LEISURE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.RESIDENCE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo;
import com.wanted.safewallet.domain.budget.business.facade.BudgetFacadeService;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest.BudgetOfCategoryRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequest;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetConsultResponse.BudgetConsultOfCategoryResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse.BudgetOfCategoryResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponse;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.response.aop.PageStore;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import java.time.YearMonth;
import java.util.List;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Import(PageStore.class)
@WithMockCustomUser
@WebMvcTest(BudgetController.class)
class BudgetControllerTest extends AbstractRestDocsTest {

    @MockBean
    BudgetFacadeService budgetFacadeService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("월별 예산 설정 컨트롤러 테스트 : 성공")
    @Test
    void setUpBudget() throws Exception {
        //given
        BudgetSetUpResponse response = new BudgetSetUpResponse(List.of(
            new BudgetOfCategoryResponse(1L, 1L, CategoryType.FOOD, 10000L),
            new BudgetOfCategoryResponse(2L, 2L, CategoryType.TRAFFIC, 5000L)));
        given(budgetFacadeService.setUpBudget(anyString(), any(BudgetSetUpRequest.class))).willReturn(response);

        //when, then
        BudgetSetUpRequest request = new BudgetSetUpRequest(YearMonth.now(),
            List.of(new BudgetOfCategoryRequest(1L, CategoryType.FOOD, 10000L),
                new BudgetOfCategoryRequest(2L, CategoryType.TRAFFIC, 5000L)));
        restDocsMockMvc.perform(post("/api/budgets")
                .content(asJsonString(request))
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
        then(budgetFacadeService).should(times(1))
            .setUpBudget(anyString(), any(BudgetSetUpRequest.class));
    }

    @DisplayName("월별 예산 설정 컨트롤러 테스트 : 실패")
    @Test
    void setUpBudget_validation_fail() throws Exception {
        //given
        BudgetSetUpRequest request = new BudgetSetUpRequest(
            YearMonth.now().minusMonths(1), List.of());

        //when, then
        mockMvc.perform(post("/api/budgets")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("budgetYearMonth"), containsString("budgetList"))))
            .andDo(print());
        then(budgetFacadeService).should(times(0))
            .setUpBudget(anyString(), any(BudgetSetUpRequest.class));
    }

    @DisplayName("월별 예산 수정 컨트롤러 테스트 : 성공")
    @Test
    void updateBudget() throws Exception {
        //given
        Long budgetId = 1L;
        BudgetUpdateRequest request = new BudgetUpdateRequest(YearMonth.now(),
            2L, CategoryType.TRAFFIC, 20000L);
        BudgetUpdateResponse response = new BudgetUpdateResponse(budgetId,
            request.getBudgetYearMonth(), request.getCategoryId(), request.getType(), request.getAmount());
        given(budgetFacadeService.updateBudget(anyString(), anyLong(), any(BudgetUpdateRequest.class)))
            .willReturn(response);

        //when, then
        restDocsMockMvc.perform(put("/api/budgets/" + budgetId)
                .content(asJsonString(request))
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
        BudgetUpdateRequest request = new BudgetUpdateRequest(null,
            2L, CategoryType.TRAFFIC, -1L);

        //when, then
        mockMvc.perform(put("/api/budgets/" + budgetId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("budgetYearMonth: 월별 예산 수정값이 널일 수 없습니다."),
                containsString("amount: 월별 예산 금액은 0원 이상이어야 합니다."))))
            .andDo(print());
    }

    @DisplayName("월별 예산 설계 컨트롤러 테스트 : 성공")
    @Test
    void consultBudget() throws Exception {
        //given
        Long totalAmount = 1_000_000L;
        List<BudgetConsultOfCategoryResponse> budgetConsultList = List.of(
            new BudgetConsultOfCategoryResponse(1L, FOOD, 200_000L),
            new BudgetConsultOfCategoryResponse(2L, TRAFFIC, 150_000L),
            new BudgetConsultOfCategoryResponse(3L, RESIDENCE, 500_000L),
            new BudgetConsultOfCategoryResponse(4L, CLOTHING, 100_000L),
            new BudgetConsultOfCategoryResponse(5L, LEISURE, 30_000L),
            new BudgetConsultOfCategoryResponse(6L, ETC, 20_000L));
        BudgetConsultResponse response = new BudgetConsultResponse(budgetConsultList);
        given(budgetFacadeService.consultBudget(anyString(), anyLong())).willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/budgets/consult")
                .param("totalAmount", String.valueOf(totalAmount))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.budgetConsultList", hasSize(6)))
            .andDo(restDocs.document(
                queryParameters(
                    parameterWithName("totalAmount").description("예산 설계 총액")
                        .attributes(key("constraints").value("0원 이상 100,000,000원 이하"))
                ),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("budgetConsultList").description("예산 설계 결과 리스트"),
                    fieldWithPath("budgetConsultList[].categoryId").description("카테고리 id"),
                    fieldWithPath("budgetConsultList[].type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("budgetConsultList[].amount").description("카테고리 별 예산 금액"))
            ));
    }

    @DisplayName("월별 예산 설계 컨트롤러 테스트 : 실패")
    @Test
    void consultBudget_validation_fail() throws Exception {
        //given
        Long totalAmount = 1000_000_000L;

        //when, then
        mockMvc.perform(get("/api/budgets/consult")
                .param("totalAmount", String.valueOf(totalAmount))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("월별 예산 설계를 위한 예산 총액은 100000000원 이하이어야 합니다.")))
            .andDo(print());
    }
}
