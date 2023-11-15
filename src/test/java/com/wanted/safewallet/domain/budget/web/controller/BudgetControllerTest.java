package com.wanted.safewallet.domain.budget.web.controller;

import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto.BudgetByCategory;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
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

@WebMvcTest(BudgetController.class)
class BudgetControllerTest {

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
        BudgetSetUpRequestDto requestDto = new BudgetSetUpRequestDto(YearMonth.of(2023, 11),
            List.of(new BudgetSetUpRequestDto.BudgetByCategory(1L, CategoryType.FOOD, 10000L),
                new BudgetSetUpRequestDto.BudgetByCategory(2L, CategoryType.TRAFFIC, 5000L)));
        mockMvc.perform(post("/api/budgets")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.budgetList", hasSize(2)))
            .andDo(print());
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
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", AllOf.allOf(
                containsString("budgetYearMonth"), containsString("budgetList"))))
            .andDo(print());
        then(budgetService).should(times(0))
            .setUpBudget(anyString(), any(BudgetSetUpRequestDto.class));
    }
}
