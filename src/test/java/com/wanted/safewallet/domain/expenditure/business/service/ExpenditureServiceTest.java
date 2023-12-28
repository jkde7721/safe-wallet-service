package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.CLOTHING;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.LEISURE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.RESIDENCE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_MONTH;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_EXPENDITURE;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_EXPENDITURE;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static com.wanted.safewallet.utils.Fixtures.anExpenditure;
import static com.wanted.safewallet.utils.Fixtures.anUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureUpdateDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto.ExpenditureAmountOfCategoryDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureImageRepository;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenditureServiceTest {

    @InjectMocks
    ExpenditureService expenditureService;

    @Mock
    ExpenditureRepository expenditureRepository;

    @Mock
    ExpenditureImageRepository expenditureImageRepository;

    @DisplayName("지출 내역 수정 서비스 테스트 : 성공")
    @Test
    void updateExpenditure() {
        //given
        LocalDateTime now = LocalDateTime.now();
        long amount = 10000;
        Expenditure expenditure = anExpenditure().id(1L)
            .user(anUser().id("testUserId").build())
            .category(aCategory().id(1L).type(FOOD).build())
            .expenditureDate(now).amount(amount).build();
        ExpenditureUpdateDto updateDto = new ExpenditureUpdateDto(
            now.plusDays(2), amount / 2, 2L, CategoryType.TRAFFIC, "하루 교통비", "지출을 줄이자");

        //when
        expenditureService.updateExpenditure(expenditure, updateDto);

        //then
        assertThat(expenditure.getExpenditureDate()).isEqualTo(updateDto.getExpenditureDate());
        assertThat(expenditure.getAmount()).isEqualTo(updateDto.getAmount());
        assertThat(expenditure.getCategory().getId()).isEqualTo(updateDto.getCategoryId());
        assertThat(expenditure.getCategory().getType()).isEqualTo(updateDto.getType());
        assertThat(expenditure.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(expenditure.getNote()).isEqualTo(updateDto.getNote());
    }

    @DisplayName("지출 내역 삭제 서비스 테스트 : 성공")
    @Test
    void deleteExpenditure() {
        //given
        Expenditure expenditure = anExpenditure().build();

        //when
        expenditureService.deleteExpenditure(expenditure);

        //then
        InOrder inOrder = inOrder(expenditureImageRepository, expenditureRepository);
        inOrder.verify(expenditureImageRepository, times(1)).deleteAllByExpenditure(expenditure.getId());
        inOrder.verify(expenditureRepository, times(1)).delete(expenditure);
    }

    @DisplayName("지출 통계 서비스 테스트 : 성공")
    @Test
    void produceExpenditureStats() {
        //given
        String userId = "testUserId";
        ExpenditureStatsDateDto expenditureStatsDateDto = new ExpenditureStatsDateDto(
            LocalDate.of(2023, 12, 20), LAST_MONTH);
        List<ExpenditureAmountOfCategoryDto> expenditureAmountOfCategoryList = List.of(
            new ExpenditureAmountOfCategoryDto(aCategory().id(1L).type(FOOD).build(), 150_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(2L).type(TRAFFIC).build(), 100_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(3L).type(RESIDENCE).build(), 500_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(4L).type(CLOTHING).build(), 100_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(5L).type(LEISURE).build(), 50_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(6L).type(ETC).build(), 5_000L));
        ExpenditureAmountOfCategoryListDto expenditureAmountOfCategoryListDto = new ExpenditureAmountOfCategoryListDto(expenditureAmountOfCategoryList);
        given(expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(expenditureAmountOfCategoryListDto);

        //when
        ExpenditureStatsDto statsDto = expenditureService.produceExpenditureStats(userId, expenditureStatsDateDto);

        //then
        then(expenditureRepository).should(times(2)).findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        assertThat(statsDto.getTotalConsumptionRate()).isEqualTo(100L);
        assertThat(statsDto.getConsumptionRateByCategory()).containsExactlyInAnyOrderEntriesOf(Map.of(
            aCategory().type(FOOD).build(), 100L, aCategory().type(TRAFFIC).build(), 100L,
            aCategory().type(RESIDENCE).build(), 100L, aCategory().type(CLOTHING).build(), 100L,
            aCategory().type(LEISURE).build(), 100L, aCategory().type(ETC).build(), 100L));
    }

    @DisplayName("현재 로그인한 사용자의 유효한 지출 내역 조회 테스트 : 실패 - 접근 권한 없는 지출 내역")
    @Test
    void getValidExpenditure_forbidden_fail() {
        //given
        String userId = "testUserId";
        String anotherUserId = "otherUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = anExpenditure().id(expenditureId)
            .user(anUser().id(anotherUserId).build()).build();
        given(expenditureRepository.findById(anyLong())).willReturn(Optional.of(expenditure));

        //when, then
        assertThatThrownBy(() -> expenditureService.getValidExpenditure(userId, expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(FORBIDDEN_EXPENDITURE);
    }

    @DisplayName("해당 지출 내역 식별자로 조회 테스트 : 실패 - 해당 지출 내역 존재하지 않음")
    @Test
    void getExpenditure_notFound_fail() {
        //given
        Long expenditureId = 1L;
        given(expenditureRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> expenditureService.getExpenditure(expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_EXPENDITURE);
    }

    @DisplayName("현재 로그인한 사용자의 유효한 지출 내역 조회 테스트 with 카테고리, 이미지 : 실패 - 접근 권한 없는 지출 내역")
    @Test
    void getValidExpenditureWithCategoryAndImages_forbidden_fail() {
        //given
        String userId = "testUserId";
        String anotherUserId = "otherUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = anExpenditure().id(expenditureId)
            .user(anUser().id(anotherUserId).build()).build();
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.of(expenditure));

        //when, then
        assertThatThrownBy(() -> expenditureService.getValidExpenditureWithCategoryAndImages(userId, expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(FORBIDDEN_EXPENDITURE);
    }

    @DisplayName("해당 지출 내역 식별자로 조회 테스트 with 카테고리, 이미지 : 실패 - 해당 지출 내역 존재하지 않음")
    @Test
    void getExpenditureWithCategoryAndImages_notFound_fail() {
        //given
        Long expenditureId = 1L;
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> expenditureService.getExpenditureWithCategoryAndImages(expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_EXPENDITURE);
    }
}
