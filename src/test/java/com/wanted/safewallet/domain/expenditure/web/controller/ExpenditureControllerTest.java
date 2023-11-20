package com.wanted.safewallet.domain.expenditure.web.controller;

import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.global.config.SecurityConfig;
import java.time.LocalDate;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@Import(SecurityConfig.class)
@WebMvcTest(ExpenditureController.class)
class ExpenditureControllerTest {

    @MockBean
    ExpenditureService expenditureService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("지출 내역 생성 컨트롤러 테스트 : 성공")
    @Test
    void createExpenditure() throws Exception {
        //given
        ExpenditureCreateResponseDto responseDto = new ExpenditureCreateResponseDto(1L);
        given(expenditureService.createExpenditure(anyString(), any(ExpenditureCreateRequestDto.class)))
            .willReturn(responseDto);

        //when, then
        ExpenditureCreateRequestDto requestDto = new ExpenditureCreateRequestDto(
            LocalDate.now(), 10000L, 1L, CategoryType.FOOD, "");
        mockMvc.perform(post("/api/expenditures")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.expenditureId").exists())
            .andDo(print());
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
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("expenditureDate"), containsString("amount"))))
            .andDo(print());
        then(expenditureService).should(times(0)).createExpenditure(anyString(), any(ExpenditureCreateRequestDto.class));
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
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("expenditureDate"), containsString("amount"))))
            .andDo(print());
        then(expenditureService).should(times(0)).updateExpenditure(
            anyString(), anyLong(), any(ExpenditureUpdateRequestDto.class));
    }
}
