package com.wanted.safewallet.domain.expenditure.web.controller;

import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
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
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator;
import com.wanted.safewallet.docs.common.DocsPopupLinkGenerator.DocsPopupInfo;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponseDto.ExpenditureResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TotalAmountByCategoryResponseDto;
import com.wanted.safewallet.global.dto.response.PageResponse;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import java.time.LocalDate;
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

    @Autowired
    MockMvc mockMvc;

    @DisplayName("지출 내역 상세 조회 컨트롤러 테스트 : 성공")
    @Test
    void getExpenditureDetails() throws Exception {
        //given
        ExpenditureDetailsResponseDto responseDto = new ExpenditureDetailsResponseDto(
            LocalDate.now(), 20000L, 1L, CategoryType.FOOD, "식비를 줄이자!");
        given(expenditureService.getExpenditureDetails(anyString(), anyLong())).willReturn(responseDto);

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
                    fieldWithPath("type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("note").description("지출 관련 메모")
                )
            ));
        then(expenditureService).should(times(1))
            .getExpenditureDetails(anyString(), anyLong());
    }

    @DisplayName("지출 내역 목록 조회 컨트롤러 테스트 : 성공")
    @Test
    void searchExpenditure() throws Exception {
        //given
        ExpenditureSearchResponseDto responseDto = ExpenditureSearchResponseDto.builder()
            .totalAmount(30000L)
            .totalAmountListByCategory(List.of(
                TotalAmountByCategoryResponseDto.builder().categoryId(1L).type(CategoryType.FOOD)
                    .totalAmount(25000L).build(),
                TotalAmountByCategoryResponseDto.builder().categoryId(2L).type(CategoryType.TRAFFIC)
                    .totalAmount(5000L).build()))
            .expenditureListByDate(List.of(
                ExpenditureListByDateResponseDto.builder()
                    .expenditureDate(LocalDate.of(2023, 11, 17))
                    .expenditureList(List.of(
                        ExpenditureResponseDto.builder().expenditureId(3L).amount(5000L)
                            .categoryId(2L).type(CategoryType.TRAFFIC).note("버스비").build(),
                        ExpenditureResponseDto.builder().expenditureId(2L).amount(4000L)
                            .categoryId(1L).type(CategoryType.FOOD).note("편의점 점심").build())).build(),
                ExpenditureListByDateResponseDto.builder()
                    .expenditureDate(LocalDate.of(2023, 11, 15))
                    .expenditureList(List.of(
                        ExpenditureResponseDto.builder().expenditureId(1L).amount(21000L)
                            .categoryId(1L).type(CategoryType.FOOD).note("식비를 아끼자!").build())).build()))
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
            any(Pageable.class))).willReturn(responseDto);

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
                        .attributes(key("default").value(0)),
                    parameterWithName("maxAmount").description("지출 조회 최대 금액").optional()
                        .attributes(key("constraints").value("100,000,000원 이하"))
                        .attributes(key("default").value(1000_000)),
                    parameterWithName("excepts").description("지출 합계 제외 지출 id").optional()
                        .attributes(key("default").value("조회된 모든 지출을 합계에 포함"))),

                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("totalAmount").description("총 지출 합계"),
                    fieldWithPath("totalAmountListByCategory").description("카테고리 별 지출 합계 목록"),
                    fieldWithPath("totalAmountListByCategory[].categoryId").description("카테고리 id"),
                    fieldWithPath("totalAmountListByCategory[].type")
                        .description(DocsPopupLinkGenerator.generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("totalAmountListByCategory[].totalAmount").description("카테고리 별 지출 합계"),
                    fieldWithPath("expenditureListByDate").description("날짜 별 지출 상세 목록"),
                    fieldWithPath("expenditureListByDate[].expenditureDate").description("지출 날짜"),
                    fieldWithPath("expenditureListByDate[].expenditureList").description("날짜 별 지출"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].expenditureId").description("지출 id"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].amount").description("지출 금액"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].categoryId").description("카테고리 id"),
                    fieldWithPath("expenditureListByDate[].expenditureList[].type")
                        .description(DocsPopupLinkGenerator.generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("expenditureListByDate[].expenditureList[].note").description("지출 관련 메모"),
                    subsectionWithPath("paging").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.PAGING_RESPONSE)))));
    }

    @DisplayName("지출 내역 목록 조회 시 합계 제외 컨트롤러 테스트 : 성공")
    @Test
    void searchExpenditureExcepts() throws Exception {
        //given
        ExpenditureSearchExceptsResponseDto responseDto = ExpenditureSearchExceptsResponseDto.builder()
            .totalAmount(26000L)
            .totalAmountListByCategory(List.of(
                TotalAmountByCategoryResponseDto.builder().categoryId(1L).type(CategoryType.FOOD)
                    .totalAmount(21000L).build(),
                TotalAmountByCategoryResponseDto.builder().categoryId(2L).type(CategoryType.TRAFFIC)
                    .totalAmount(5000L).build()))
            .build();
        LocalDate startDate = LocalDate.of(2023, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 30);
        List<Long> categories = List.of(1L, 2L);
        Long minAmount = 1000L;
        Long maxAmount = 50000L;
        List<Long> excepts = List.of(2L);
        given(expenditureService.searchExpenditureExcepts(anyString(), any(ExpenditureSearchCond.class)))
            .willReturn(responseDto);

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
                        .attributes(key("default").value(0)),
                    parameterWithName("maxAmount").description("지출 조회 최대 금액").optional()
                        .attributes(key("constraints").value("100,000,000원 이하"))
                        .attributes(key("default").value(1000_000)),
                    parameterWithName("excepts").description("지출 합계 제외 지출 id").optional()
                        .attributes(key("default").value("조회된 모든 지출을 합계에 포함"))),

                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("totalAmount").description("총 지출 합계(excepts 파라미터에서 지정한 지출은 제외)"),
                    fieldWithPath("totalAmountListByCategory").description("카테고리 별 지출 합계 목록"),
                    fieldWithPath("totalAmountListByCategory[].categoryId").description("카테고리 id"),
                    fieldWithPath("totalAmountListByCategory[].type")
                        .description(DocsPopupLinkGenerator.generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("totalAmountListByCategory[].totalAmount")
                        .description("카테고리 별 지출 합계(excepts 파라미터에서 지정한 지출은 제외)"))));
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
        ExpenditureCreateResponseDto responseDto = new ExpenditureCreateResponseDto(1L);
        given(expenditureService.createExpenditure(anyString(), any(ExpenditureCreateRequestDto.class)))
            .willReturn(responseDto);

        //when, then
        ExpenditureCreateRequestDto requestDto = new ExpenditureCreateRequestDto(
            LocalDate.now(), 20000L, 1L, CategoryType.FOOD, "식비를 줄이자!");
        restDocsMockMvc.perform(post("/api/expenditures")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.expenditureId").exists())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("expenditureDate").description("지출 발생 년월일")
                        .attributes(key("formats").value("yyyy-M-d 또는 yyyy/M/d 또는 yyyy.M.d")),
                    fieldWithPath("amount").description("지출 금액")
                        .attributes(key("constraints").value("0원 이상")),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("note").description("지출 관련 메모").optional()),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("expenditureId").description("생성된 지출 id"))));
        then(expenditureService).should(times(1)).createExpenditure(anyString(), any(ExpenditureCreateRequestDto.class));
    }

    @DisplayName("지출 내역 생성 컨트롤러 테스트 : 실패")
    @Test
    void createExpenditure_validation_fail() throws Exception {
        //given
        ExpenditureCreateRequestDto requestDto = new ExpenditureCreateRequestDto(
            null, -1L, 1L, CategoryType.FOOD, "");

        //when, then
        mockMvc.perform(post("/api/expenditures")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("expenditureDate"), containsString("amount"))))
            .andDo(print());
        then(expenditureService).should(times(0)).createExpenditure(anyString(), any(ExpenditureCreateRequestDto.class));
    }

    @DisplayName("지출 내역 수정 컨트롤러 테스트 : 성공")
    @Test
    void updateExpenditure() throws Exception {
        //given
        ExpenditureUpdateRequestDto requestDto = new ExpenditureUpdateRequestDto(
            LocalDate.now(), 20000L, 1L, CategoryType.FOOD, "식비를 줄이자!");

        //when, then
        restDocsMockMvc.perform(put("/api/expenditures/1")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("expenditureDate").description("지출 발생 년월일")
                        .attributes(key("formats").value("yyyy-M-d 또는 yyyy/M/d 또는 yyyy.M.d")),
                    fieldWithPath("amount").description("지출 금액")
                        .attributes(key("constraints").value("0원 이상")),
                    fieldWithPath("categoryId").description("카테고리 id"),
                    fieldWithPath("type").description(DocsPopupLinkGenerator
                        .generatePopupLink(DocsPopupInfo.CATEGORY_TYPE)),
                    fieldWithPath("note").description("지출 관련 메모").optional())));
        then(expenditureService).should(times(1)).updateExpenditure(
            anyString(), anyLong(), any(ExpenditureUpdateRequestDto.class));
    }

    @DisplayName("지출 내역 수정 컨트롤러 테스트 : 실패")
    @Test
    void updateExpenditure_validation_fail() throws Exception {
        //given
        ExpenditureUpdateRequestDto requestDto = new ExpenditureUpdateRequestDto(
            null, -1L, 1L, CategoryType.FOOD, "");

        //when, then
        mockMvc.perform(put("/api/expenditures/1")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("expenditureDate"), containsString("amount"))))
            .andDo(print());
        then(expenditureService).should(times(0)).updateExpenditure(
            anyString(), anyLong(), any(ExpenditureUpdateRequestDto.class));
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
}
