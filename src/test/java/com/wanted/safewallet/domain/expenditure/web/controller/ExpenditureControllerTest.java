package com.wanted.safewallet.domain.expenditure.web.controller;

import static com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo.CATEGORY_TYPE;
import static com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo.FINANCE_STATUS;
import static com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo.PAGING_RESPONSE;
import static com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo.STATS_CRITERIA;
import static com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.generatePopupLink;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.CLOTHING;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.LEISURE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.RESIDENCE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.BAD;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.EXCELLENT;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.GOOD;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_MONTH;
import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static java.time.temporal.ChronoUnit.DAYS;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureConsultService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureDailyStatsService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponse.ExpenditureResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse.ConsumptionRateOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse.TodayExpenditureConsultOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse.YesterdayExpenditureDailyStatsOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureAmountOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import com.wanted.safewallet.global.dto.response.PageResponse;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WithMockCustomUser
@WebMvcTest(ExpenditureController.class)
class ExpenditureControllerTest extends AbstractRestDocsTest {

    @MockBean
    ExpenditureService expenditureService;

    @MockBean
    ExpenditureConsultService expenditureConsultService;

    @MockBean
    ExpenditureDailyStatsService expenditureDailyStatsService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("지출 내역 상세 조회 컨트롤러 테스트 : 성공")
    @Test
    void getExpenditureDetails() throws Exception {
        //given
        ExpenditureDetailsResponse response = new ExpenditureDetailsResponse(
            LocalDateTime.now(), 20000L, 1L, CategoryType.FOOD, "점심 커피챗", "식비를 줄이자!",
            List.of("https://image1", "https://image2", "https://image3"));
        given(expenditureService.getExpenditureDetails(anyString(), anyLong())).willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/expenditures/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists())
            .andDo(restDocs.document(
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("expenditureDate").description("지출 발생 년월일"),
                    fieldWithPath("amount").description("지출 금액"),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("title").description("지출 제목"),
                    fieldWithPath("note").description("지출 관련 메모"),
                    fieldWithPath("imageUrls").description("지출 이미지 URL")
                )
            ));
        then(expenditureService).should(times(1))
            .getExpenditureDetails(anyString(), anyLong());
    }

    @DisplayName("지출 내역 목록 조회 컨트롤러 테스트 : 성공")
    @Test
    void searchExpenditure() throws Exception {
        //given
        ExpenditureSearchResponse response = ExpenditureSearchResponse.builder()
            .totalAmount(30000L)
            .expenditureAmountOfCategoryList(List.of(
                ExpenditureAmountOfCategoryResponse.builder().categoryId(1L).type(CategoryType.FOOD)
                    .amount(25000L).build(),
                ExpenditureAmountOfCategoryResponse.builder().categoryId(2L).type(CategoryType.TRAFFIC)
                    .amount(5000L).build()))
            .expenditureListByDate(List.of(
                ExpenditureListByDateResponse.builder()
                    .expenditureDate(LocalDate.of(2023, 11, 17))
                    .expenditureList(List.of(
                        ExpenditureResponse.builder().expenditureId(3L).amount(5000L)
                            .categoryId(2L).type(CategoryType.TRAFFIC).title("하루 교통비").build(),
                        ExpenditureResponse.builder().expenditureId(2L).amount(4000L)
                            .categoryId(1L).type(CategoryType.FOOD).title("편의점 점심").build())).build(),
                ExpenditureListByDateResponse.builder()
                    .expenditureDate(LocalDate.of(2023, 11, 15))
                    .expenditureList(List.of(
                        ExpenditureResponse.builder().expenditureId(1L).amount(21000L)
                            .categoryId(1L).type(CategoryType.FOOD).title("점심 커피챗").build())).build()))
            .paging(PageResponse.builder()
                .pageNumber(1).pageSize(3).numberOfElements(3).totalPages(4).totalElements(12L)
                .first(true).last(false).empty(false).build())
            .build();
        LocalDate startDate = LocalDate.of(2023, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 30);
        List<Long> categories = List.of(1L, 2L);
        Long minAmount = 1000L;
        Long maxAmount = 50000L;
        given(expenditureService.searchExpenditure(anyString(), any(ExpenditureSearchCond.class),
            any(Pageable.class))).willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/expenditures")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("categories", collectionToCommaDelimitedString(categories))
                .param("minAmount", String.valueOf(minAmount))
                .param("maxAmount", String.valueOf(maxAmount))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists())
            .andDo(restDocs.document(
                queryParameters(
                    parameterWithName("startDate").description("지출 조회 시작 날짜").optional()
                        .attributes(key("formats").value("yyyy-MM-dd"))
                        .attributes(key("default").value("현재 날짜 기준 1달 전")),
                    parameterWithName("endDate").description("지출 조회 종료 날짜").optional()
                        .attributes(key("formats").value("yyyy-MM-dd"))
                        .attributes(key("constraints").value("지출 조회 가능 기간은 최대 1년"))
                        .attributes(key("default").value("현재 날짜")),
                    parameterWithName("categories").description("지출 조회 카테고리 id").optional()
                        .attributes(key("default").value("모든 카테고리 조회")),
                    parameterWithName("minAmount").description("지출 조회 최소 금액").optional()
                        .attributes(key("constraints").value("0원 이상"))
                        .attributes(key("default").value("0원")),
                    parameterWithName("maxAmount").description("지출 조회 최대 금액").optional()
                        .attributes(key("constraints").value("100,000,000원 이하"))
                        .attributes(key("default").value("1000,000원")),
                    parameterWithName("excepts").description("지출 합계 제외 지출 id").optional()
                        .attributes(key("default").value("조회된 모든 지출을 합계에 포함"))),

                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("totalAmount").description("총 지출 합계"),
                    fieldWithPath("expenditureAmountOfCategoryList").description("카테고리 별 지출 금액 목록"),
                    fieldWithPath("expenditureAmountOfCategoryList[].categoryId").description("카테고리 id"),
                    fieldWithPath("expenditureAmountOfCategoryList[].type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("expenditureAmountOfCategoryList[].amount").description("카테고리 별 지출 금액"),
                    fieldWithPath("expenditureListByDate").description("날짜 별 지출 상세 목록"),
                    fieldWithPath("expenditureListByDate[].expenditureDate").description("지출 날짜"),
                    fieldWithPath("expenditureListByDate[].expenditureList").description("지출 상세 목록"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].expenditureId").description("지출 id"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].amount").description("지출 금액"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].categoryId").description("카테고리 id"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("expenditureListByDate[].expenditureList[].title").description("지출 제목"),
                    subsectionWithPath("paging").description(generatePopupLink(PAGING_RESPONSE)))));
    }

    @DisplayName("지출 내역 목록 조회 시 합계 제외 컨트롤러 테스트 : 성공")
    @Test
    void searchExpenditureExcepts() throws Exception {
        //given
        ExpenditureSearchExceptsResponse response = ExpenditureSearchExceptsResponse.builder()
            .totalAmount(26000L)
            .expenditureAmountOfCategoryList(List.of(
                ExpenditureAmountOfCategoryResponse.builder().categoryId(1L).type(CategoryType.FOOD)
                    .amount(21000L).build(),
                ExpenditureAmountOfCategoryResponse.builder().categoryId(2L).type(CategoryType.TRAFFIC)
                    .amount(5000L).build()))
            .build();
        LocalDate startDate = LocalDate.of(2023, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 30);
        List<Long> categories = List.of(1L, 2L);
        Long minAmount = 1000L;
        Long maxAmount = 50000L;
        List<Long> excepts = List.of(2L);
        given(expenditureService.searchExpenditureExcepts(anyString(), any(ExpenditureSearchCond.class)))
            .willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/expenditures/excepts")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("categories", collectionToCommaDelimitedString(categories))
                .param("minAmount", String.valueOf(minAmount))
                .param("maxAmount", String.valueOf(maxAmount))
                .param("excepts", collectionToCommaDelimitedString(excepts))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists())
            .andDo(restDocs.document(
                queryParameters(
                    parameterWithName("startDate").description("지출 조회 시작 날짜").optional()
                        .attributes(key("formats").value("yyyy-MM-dd"))
                        .attributes(key("default").value("현재 날짜 기준 1달 전")),
                    parameterWithName("endDate").description("지출 조회 종료 날짜").optional()
                        .attributes(key("formats").value("yyyy-MM-dd"))
                        .attributes(key("constraints").value("지출 조회 가능 기간은 최대 1년"))
                        .attributes(key("default").value("현재 날짜")),
                    parameterWithName("categories").description("지출 조회 카테고리 id").optional()
                        .attributes(key("default").value("모든 카테고리 조회")),
                    parameterWithName("minAmount").description("지출 조회 최소 금액").optional()
                        .attributes(key("constraints").value("0원 이상"))
                        .attributes(key("default").value("0원")),
                    parameterWithName("maxAmount").description("지출 조회 최대 금액").optional()
                        .attributes(key("constraints").value("100,000,000원 이하"))
                        .attributes(key("default").value("1000,000원")),
                    parameterWithName("excepts").description("지출 합계 제외 지출 id").optional()
                        .attributes(key("default").value("조회된 모든 지출을 합계에 포함"))),

                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("totalAmount").description("총 지출 합계(excepts 파라미터에서 지정한 지출은 제외)"),
                    fieldWithPath("expenditureAmountOfCategoryList").description("카테고리 별 지출 금액 목록"),
                    fieldWithPath("expenditureAmountOfCategoryList[].categoryId").description("카테고리 id"),
                    fieldWithPath("expenditureAmountOfCategoryList[].type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("expenditureAmountOfCategoryList[].amount")
                        .description("카테고리 별 지출 금액(excepts 파라미터에서 지정한 지출은 제외)"))));
    }

    @DisplayName("지출 내역 목록 조회 컨트롤러 테스트 : 실패 - 가능한 검색 기간 초과")
    @Test
    void searchExpenditure_validation_fail() throws Exception {
        //given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(13);

        //when, then
        mockMvc.perform(get("/api/expenditures")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("조회 가능 기간은 최대 1년입니다.")))
            .andDo(print());
    }

    @DisplayName("지출 내역 목록 조회 시 합계 제외 컨트롤러 테스트 : 실패 - 검색 지출 금액 validation 실패")
    @Test
    void searchExpenditureExcepts_validation_fail() throws Exception {
        //given
        Long minAmount = -1L;
        Long maxAmount = 1000_000_000L;

        //when, then
        mockMvc.perform(get("/api/expenditures")
                .param("minAmount", String.valueOf(minAmount))
                .param("maxAmount", String.valueOf(maxAmount))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("검색 지출 금액은 0원 이상이어야 합니다."),
                containsString("검색 지출 금액은 100000000원 이하이어야 합니다.")
            )))
            .andDo(print());
    }

    @DisplayName("지출 내역 생성 컨트롤러 테스트 : 성공")
    @Test
    void createExpenditure() throws Exception {
        //given
        ExpenditureCreateResponse response = new ExpenditureCreateResponse(1L);
        given(expenditureService.createExpenditure(anyString(), any(ExpenditureCreateRequest.class)))
            .willReturn(response);

        //when, then
        ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            LocalDateTime.now().withNano(0), 20000L, 1L, CategoryType.FOOD, "점심 커피챗", "식비를 줄이자!");
        restDocsMockMvc.perform(post("/api/expenditures")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.expenditureId").exists())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("expenditureDate").description("지출 발생 년월일")
                        .attributes(key("formats").value("yyyy-M-d'T'HH:mm:ss 또는 yyyy/M/d'T'HH:mm:ss 또는 yyyy.M.d'T'HH:mm:ss")),
                    fieldWithPath("amount").description("지출 금액")
                        .attributes(key("constraints").value("0원 이상")),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("title").description("지출 제목"),
                    fieldWithPath("note").description("지출 관련 메모").optional()),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("expenditureId").description("생성된 지출 id"))));
        then(expenditureService).should(times(1)).createExpenditure(anyString(), any(
            ExpenditureCreateRequest.class));
    }

    @DisplayName("지출 내역 생성 컨트롤러 테스트 : 실패")
    @Test
    void createExpenditure_validation_fail() throws Exception {
        //given
        ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            null, -1L, 1L, CategoryType.FOOD, "점심 커피챗", "");

        //when, then
        mockMvc.perform(post("/api/expenditures")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("expenditureDate"), containsString("amount"))))
            .andDo(print());
        then(expenditureService).should(times(0)).createExpenditure(anyString(), any(
            ExpenditureCreateRequest.class));
    }

    @DisplayName("지출 내역 수정 컨트롤러 테스트 : 성공")
    @Test
    void updateExpenditure() throws Exception {
        //given
        ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(
            LocalDateTime.now().withNano(0), 20000L, 1L, CategoryType.FOOD, "점심 커피챗", "식비를 줄이자!");

        //when, then
        restDocsMockMvc.perform(put("/api/expenditures/1")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("expenditureDate").description("지출 발생 년월일")
                        .attributes(key("formats").value("yyyy-M-d'T'HH:mm:ss 또는 yyyy/M/d'T'HH:mm:ss 또는 yyyy.M.d'T'HH:mm:ss")),
                    fieldWithPath("amount").description("지출 금액")
                        .attributes(key("constraints").value("0원 이상")),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("title").description("지출 제목"),
                    fieldWithPath("note").description("지출 관련 메모").optional())));
        then(expenditureService).should(times(1)).updateExpenditure(
            anyString(), anyLong(), any(ExpenditureUpdateRequest.class));
    }

    @DisplayName("지출 내역 수정 컨트롤러 테스트 : 실패")
    @Test
    void updateExpenditure_validation_fail() throws Exception {
        //given
        ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(
            null, -1L, 1L, CategoryType.FOOD, "점심 커피챗", "");

        //when, then
        mockMvc.perform(put("/api/expenditures/1")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("expenditureDate"), containsString("amount"))))
            .andDo(print());
        then(expenditureService).should(times(0)).updateExpenditure(
            anyString(), anyLong(), any(ExpenditureUpdateRequest.class));
    }

    @DisplayName("지출 내역 삭제 컨트롤러 테스트 : 성공")
    @Test
    void deleteExpenditure() throws Exception {
        //given
        //when, then
        restDocsMockMvc.perform(delete("/api/expenditures/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").doesNotExist());
        then(expenditureService).should(times(1))
            .deleteExpenditure(anyString(), anyLong());
    }

    @DisplayName("지출 통계 컨트롤러 테스트 : 성공")
    @Test
    void produceExpenditureStats() throws Exception {
        //given
        LocalDate currentEndDate = LocalDate.now();
        LocalDate currentStartDate = LocalDate.of(currentEndDate.getYear(), currentEndDate.getMonth(), 1);
        LocalDate criteriaStartDate = currentStartDate.minusMonths(1);
        LocalDate criteriaEndDate = criteriaStartDate.plusDays(DAYS.between(currentStartDate, currentEndDate));
        Long totalConsumptionRate = 110L;
        List<ConsumptionRateOfCategoryResponse> consumptionRateOfCategoryList = List.of(
            new ConsumptionRateOfCategoryResponse(1L, FOOD, 150L),
            new ConsumptionRateOfCategoryResponse(2L, TRAFFIC, 130L),
            new ConsumptionRateOfCategoryResponse(3L, RESIDENCE, 100L),
            new ConsumptionRateOfCategoryResponse(4L, CLOTHING, 70L),
            new ConsumptionRateOfCategoryResponse(5L, LEISURE, 55L),
            new ConsumptionRateOfCategoryResponse(6L, ETC, 100L));
        ExpenditureStatsResponse response = ExpenditureStatsResponse.builder()
            .currentStartDate(currentStartDate).currentEndDate(currentEndDate)
            .criteriaStartDate(criteriaStartDate).criteriaEndDate(criteriaEndDate)
            .totalConsumptionRate(totalConsumptionRate)
            .consumptionRateOfCategoryList(consumptionRateOfCategoryList).build();
        given(expenditureService.produceExpenditureStats(anyString(), any(StatsCriteria.class)))
            .willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/expenditures/stats")
                .param("criteria", LAST_MONTH.name())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                queryParameters(
                    parameterWithName("criteria").description(generatePopupLink(STATS_CRITERIA))
                        .attributes(key("default").value(LAST_MONTH)).optional()),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("currentStartDate").description("지출 통계 대상 시작일"),
                    fieldWithPath("currentEndDate").description("지출 통계 대상 종료일 (오늘 날짜)"),
                    fieldWithPath("criteriaStartDate").description("지출 통계 기준 시작일"),
                    fieldWithPath("criteriaEndDate").description("지출 통계 기준 종료일"),
                    fieldWithPath("totalConsumptionRate").description("지난 년도, 달, 주 대비 전체 소비율(%)"),
                    fieldWithPath("consumptionRateOfCategoryList").description("카테고리 별 소비율 목록"),
                    fieldWithPath("consumptionRateOfCategoryList[].categoryId").description("카테고리 id"),
                    fieldWithPath("consumptionRateOfCategoryList[].type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("consumptionRateOfCategoryList[].consumptionRate")
                        .description("지난 년도, 달, 주 대비 해당 카테고리의 소비율(%)"))
            ));
    }

    @DisplayName("오늘 지출 추천 컨트롤러 테스트 : 성공")
    @Test
    void consultTodayExpenditure() throws Exception {
        //given
        List<TodayExpenditureConsultOfCategoryResponse> todayExpenditureConsultOfCategoryList = List.of(
            new TodayExpenditureConsultOfCategoryResponse(1L, FOOD, 10800L, EXCELLENT),
            new TodayExpenditureConsultOfCategoryResponse(2L, TRAFFIC, 7200L, EXCELLENT),
            new TodayExpenditureConsultOfCategoryResponse(3L, RESIDENCE, 300000L, GOOD),
            new TodayExpenditureConsultOfCategoryResponse(4L, CLOTHING, 15000L, BAD),
            new TodayExpenditureConsultOfCategoryResponse(5L, LEISURE, 10000L, GOOD),
            new TodayExpenditureConsultOfCategoryResponse(6L, ETC, 3600L, EXCELLENT));
        TodayExpenditureConsultResponse response = new TodayExpenditureConsultResponse(
            346600L, EXCELLENT, todayExpenditureConsultOfCategoryList);
        given(expenditureConsultService.consultTodayExpenditure(anyString())).willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/expenditures/consult")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.todayExpenditureConsultOfCategoryList", hasSize(6)))
            .andDo(restDocs.document(
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("totalAmount").description("오늘 지출 추천 총액"),
                    fieldWithPath("totalFinanceStatus").description(generatePopupLink(FINANCE_STATUS)),
                    fieldWithPath("todayExpenditureConsultOfCategoryList").description("카테고리 별 오늘 지출 추천 목록"),
                    fieldWithPath("todayExpenditureConsultOfCategoryList[].categoryId").description("카테고리 id"),
                    fieldWithPath("todayExpenditureConsultOfCategoryList[].type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("todayExpenditureConsultOfCategoryList[].amount").description("오늘 지출 추천 금액"),
                    fieldWithPath("todayExpenditureConsultOfCategoryList[].financeStatus").description(generatePopupLink(FINANCE_STATUS)))
            ));
    }

    @DisplayName("어제 지출 안내 컨트롤러 테스트 : 성공")
    @Test
    void produceYesterdayExpenditureDailyStats() throws Exception {
        //given
        List<YesterdayExpenditureDailyStatsOfCategoryResponse> yesterdayExpenditureDailyStatsOfCategoryList = List.of(
            new YesterdayExpenditureDailyStatsOfCategoryResponse(1L, FOOD, 9600L, 9600L, 100L),
            new YesterdayExpenditureDailyStatsOfCategoryResponse(2L, TRAFFIC, 6400L, 3200L, 50L),
            new YesterdayExpenditureDailyStatsOfCategoryResponse(3L, RESIDENCE, 0L, 0L, 0L),
            new YesterdayExpenditureDailyStatsOfCategoryResponse(4L, CLOTHING, 50000L, 35000L, 70L),
            new YesterdayExpenditureDailyStatsOfCategoryResponse(5L, LEISURE, 10000L, 0L, 0L),
            new YesterdayExpenditureDailyStatsOfCategoryResponse(6L, ETC, 3200L, 4800L, 150L));
        YesterdayExpenditureDailyStatsResponse response = new YesterdayExpenditureDailyStatsResponse(
            52600L, yesterdayExpenditureDailyStatsOfCategoryList);
        given(expenditureDailyStatsService.produceYesterdayExpenditureDailyStats(anyString())).willReturn(response);

        //when, then
        restDocsMockMvc.perform(get("/api/expenditures/daily-stats")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.yesterdayExpenditureDailyStatsOfCategoryList", hasSize(6)))
            .andDo(restDocs.document(
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("totalAmount").description("어제 지출 총액"),
                    fieldWithPath("yesterdayExpenditureDailyStatsOfCategoryList").description("카테고리 별 어제 지출 안내 목록"),
                    fieldWithPath("yesterdayExpenditureDailyStatsOfCategoryList[].categoryId").description("카테고리 id"),
                    fieldWithPath("yesterdayExpenditureDailyStatsOfCategoryList[].type").description(generatePopupLink(CATEGORY_TYPE)),
                    fieldWithPath("yesterdayExpenditureDailyStatsOfCategoryList[].consultedAmount").description("어제 적정 지출 금액"),
                    fieldWithPath("yesterdayExpenditureDailyStatsOfCategoryList[].expendedAmount").description("어제 실제 지출 금액"),
                    fieldWithPath("yesterdayExpenditureDailyStatsOfCategoryList[].consumptionRate")
                        .description("소비율(적정 지출 금액 대비 실제 지출 금액의 비율, % 단위)"))
            ));
    }
}
