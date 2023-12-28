package com.wanted.safewallet.domain.expenditure.business.facade;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static com.wanted.safewallet.utils.Fixtures.anExpenditure;
import static com.wanted.safewallet.utils.Fixtures.anUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureUpdateDto;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureConsultService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureDailyStatsService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenditureFacadeServiceTest {

    @InjectMocks
    ExpenditureFacadeService expenditureFacadeService;

    @Spy
    ExpenditureMapper expenditureMapper;

    @Mock
    BudgetService budgetService;

    @Mock
    CategoryService categoryService;

    @Mock
    ExpenditureService expenditureService;

    @Spy
    ExpenditureConsultService expenditureConsultService;

    @Spy
    ExpenditureDailyStatsService expenditureDailyStatsService;

    @DisplayName("지출 내역 생성 퍼사드 서비스 테스트 : 성공")
    @Test
    void createExpenditure() {
        //given
        String userId = "testUserId";
        LocalDateTime now = LocalDateTime.now();
        ExpenditureCreateRequest request = new ExpenditureCreateRequest(now, 10000L,
            1L, FOOD, "점심 커피챗", "");
        Expenditure savedExpenditure = anExpenditure().id(1L)
            .user(anUser().id(userId).build())
            .category(aCategory().id(1L).type(FOOD).build())
            .expenditureDate(now).amount(10000L).title("점심 커피챗").note("").build();
        given(expenditureService.saveExpenditure(any(Expenditure.class))).willReturn(savedExpenditure);

        //when
        ExpenditureCreateResponse response = expenditureFacadeService.createExpenditure(userId, request);

        //then
        then(categoryService).should(times(1)).validateCategory(any(CategoryValidationDto.class));
        then(expenditureMapper).should(times(1)).toEntity(anyString(), any(ExpenditureCreateRequest.class));
        then(expenditureService).should(times(1)).saveExpenditure(any(Expenditure.class));
        then(expenditureMapper).should(times(1)).toResponse(any(Expenditure.class));
        assertThat(response.getExpenditureId()).isEqualTo(1L);
    }

    @DisplayName("지출 내역 수정 퍼사드 서비스 테스트 : 성공")
    @Test
    void updateExpenditure() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        LocalDateTime now = LocalDateTime.now();
        long amount = 10000;
        ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(
            now.plusDays(2), amount / 2, 2L, CategoryType.TRAFFIC, "하루 교통비", "지출을 줄이자");
        Expenditure expenditure = anExpenditure().id(expenditureId)
            .user(anUser().id(userId).build())
            .expenditureDate(now).amount(amount).build();
        given(expenditureService.getValidExpenditure(anyString(), anyLong())).willReturn(expenditure);

        //when
        expenditureFacadeService.updateExpenditure(userId, expenditureId, request);

        //then
        then(categoryService).should(times(1)).validateCategory(any(CategoryValidationDto.class));
        then(expenditureService).should(times(1)).getValidExpenditure(anyString(), anyLong());
        then(expenditureMapper).should(times(1)).toDto(any(ExpenditureUpdateRequest.class));
        then(expenditureService).should(times(1)).updateExpenditure(eq(expenditure), any(ExpenditureUpdateDto.class));
    }
}
