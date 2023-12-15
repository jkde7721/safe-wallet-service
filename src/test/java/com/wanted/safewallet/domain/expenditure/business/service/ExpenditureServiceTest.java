package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.CLOTHING;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.LEISURE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.RESIDENCE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_EXPENDITURE;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_EXPENDITURE;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto.ExpenditureAmountOfCategoryDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenditureServiceTest {

    @InjectMocks
    ExpenditureService expenditureService;

    @Spy
    ExpenditureMapper expenditureMapper;

    @Mock
    CategoryService categoryService;

    @Mock
    ExpenditureRepository expenditureRepository;

    @DisplayName("지출 내역 생성 서비스 테스트 : 성공")
    @Test
    void createExpenditure() {
        //given
        String userId = "testUserId";
        LocalDateTime now = LocalDateTime.now();
        Expenditure expenditure = Expenditure.builder().id(1L)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(now).amount(10000L).note("").build();
        given(expenditureRepository.save(any(Expenditure.class))).willReturn(expenditure);

        //when
        ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            now, 10000L, 1L, CategoryType.FOOD, "점심 커피챗", "");
        ExpenditureCreateResponse response = expenditureService.createExpenditure(userId, request);

        //then
        then(categoryService).should(times(1)).validateCategory(any(CategoryValidationDto.class));
        then(expenditureMapper).should(times(1)).toEntity(anyString(), any(ExpenditureCreateRequest.class));
        then(expenditureRepository).should(times(1)).save(any(Expenditure.class));
        then(expenditureMapper).should(times(1)).toCreateDto(any(Expenditure.class));
        assertThat(response.getExpenditureId()).isEqualTo(1L);
    }

    @DisplayName("지출 내역 수정 서비스 테스트 : 성공")
    @Test
    void updateExpenditure() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(now).amount(10000L).note("").build();
        given(expenditureRepository.findById(anyLong())).willReturn(Optional.of(expenditure));

        //when
        ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(
            now.plusDays(2), 5000L, 2L, CategoryType.TRAFFIC, "하루 교통비", "지출을 줄이자");
        expenditureService.updateExpenditure(userId, expenditureId, request);

        //then
        then(categoryService).should(times(1)).validateCategory(any(CategoryValidationDto.class));
        then(expenditureRepository).should(times(1)).findById(anyLong());
        assertThat(expenditure.getExpenditureDate()).isEqualTo(request.getExpenditureDate());
        assertThat(expenditure.getAmount()).isEqualTo(request.getAmount());
        assertThat(expenditure.getCategory().getId()).isEqualTo(request.getCategoryId());
        assertThat(expenditure.getNote()).isEqualTo(request.getNote());
    }

    @DisplayName("현재 로그인한 사용자의 유효한 지출 내역 조회 테스트 : 실패 - 접근 권한 없는 지출 내역")
    @Test
    void getValidExpenditure_forbidden_fail() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDateTime.now()).amount(10000L).note("").build();
        given(expenditureRepository.findById(anyLong())).willReturn(Optional.of(expenditure));

        //when, then
        assertThatThrownBy(() -> expenditureService.getValidExpenditure("wrong " + userId, expenditureId))
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

    @DisplayName("지출 내역 삭제 서비스 테스트 : 성공")
    @Test
    void deleteExpenditure() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDateTime.now()).amount(10000L).note("").build();
        given(expenditureRepository.findById(anyLong())).willReturn(Optional.of(expenditure));

        //when
        expenditureService.deleteExpenditure(userId, expenditureId);

        //then
        then(expenditureRepository).should(times(1)).findById(anyLong());
        assertThat(expenditure.getDeleted()).isTrue();
    }

    @DisplayName("지출 내역 상세 조회 서비스 테스트 : 성공")
    @Test
    void getExpenditureDetails() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDateTime.now()).amount(10000L).note("").build();
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.of(expenditure));

        //when
        ExpenditureDetailsResponse response = expenditureService.getExpenditureDetails(
            userId, expenditureId);

        //then
        then(expenditureRepository).should(times(1)).findByIdFetch(anyLong());
        assertThat(response).isNotNull();
    }

    @DisplayName("현재 로그인한 사용자의 유효한 지출 내역 조회 테스트 with 카테고리 : 실패 - 접근 권한 없는 지출 내역")
    @Test
    void getValidExpenditureWithCategoryAndImages() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDateTime.now()).amount(10000L).note("").build();
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.of(expenditure));

        //when, then
        assertThatThrownBy(() -> expenditureService.getValidExpenditureWithCategoryAndImages(
            "wrong " + userId, expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(FORBIDDEN_EXPENDITURE);
    }

    @DisplayName("해당 지출 내역 식별자로 조회 테스트 with 카테고리 : 실패 - 해당 지출 내역 존재하지 않음")
    @Test
    void getExpenditureWithCategoryAndImages() {
        //given
        Long expenditureId = 1L;
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> expenditureService.getExpenditureWithCategoryAndImages(expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_EXPENDITURE);
    }

    @DisplayName("지출 통계 서비스 테스트 : 성공")
    @Test
    void produceExpenditureStats() {
        //given
        String userId = "testUserId";
        StatsCriteria criteria = StatsCriteria.LAST_MONTH;
        List<ExpenditureAmountOfCategoryDto> expenditureAmountOfCategoryList = List.of(
            new ExpenditureAmountOfCategoryDto(Category.builder().id(1L).type(FOOD).build(), 150_000L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(2L).type(TRAFFIC).build(), 100_000L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(3L).type(RESIDENCE).build(), 500_000L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(4L).type(CLOTHING).build(), 100_000L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(5L).type(LEISURE).build(), 50_000L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(6L).type(ETC).build(), 5_000L));
        ExpenditureAmountOfCategoryListDto expenditureAmountOfCategoryListDto = new ExpenditureAmountOfCategoryListDto(expenditureAmountOfCategoryList);
        given(expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .willReturn(expenditureAmountOfCategoryListDto);

        //when
        ExpenditureStatsResponse response = expenditureService.produceExpenditureStats(userId, criteria);

        //then
        then(expenditureRepository).should(times(2)).findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        assertThat(MONTHS.between(response.getCriteriaStartDate(), response.getCurrentStartDate()))
            .isEqualTo(1);
        assertThat(DAYS.between(response.getCurrentStartDate(), response.getCurrentEndDate()))
            .isEqualTo(DAYS.between(response.getCriteriaStartDate(), response.getCriteriaEndDate()));
        assertThat(response.getTotalConsumptionRate()).isEqualTo(100L);
        assertThat(response.getConsumptionRateOfCategoryList()).satisfiesExactly(
            item1 -> assertThat(item1).extracting("type", "consumptionRate").containsExactly(FOOD, 100L),
            item2 -> assertThat(item2).extracting("type", "consumptionRate").containsExactly(TRAFFIC, 100L),
            item3 -> assertThat(item3).extracting("type", "consumptionRate").containsExactly(RESIDENCE, 100L),
            item4 -> assertThat(item4).extracting("type", "consumptionRate").containsExactly(CLOTHING, 100L),
            item5 -> assertThat(item5).extracting("type", "consumptionRate").containsExactly(LEISURE, 100L),
            item6 -> assertThat(item6).extracting("type", "consumptionRate").containsExactly(ETC, 100L));
    }
}
