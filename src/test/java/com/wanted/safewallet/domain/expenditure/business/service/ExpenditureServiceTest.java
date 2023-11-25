package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_EXPENDITURE;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_EXPENDITURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.LocalDate;
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
        Expenditure expenditure = Expenditure.builder().id(1L)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
        given(expenditureRepository.save(any(Expenditure.class))).willReturn(expenditure);

        //when
        ExpenditureCreateRequestDto requestDto = new ExpenditureCreateRequestDto(
            LocalDate.now(), 10000L, 1L, CategoryType.FOOD, "");
        ExpenditureCreateResponseDto responseDto = expenditureService.createExpenditure(userId, requestDto);

        //then
        then(expenditureMapper).should(times(1)).toEntity(anyString(), any(ExpenditureCreateRequestDto.class));
        then(expenditureRepository).should(times(1)).save(any(Expenditure.class));
        then(expenditureMapper).should(times(1)).toCreateDto(any(Expenditure.class));
        assertThat(responseDto.getExpenditureId()).isEqualTo(1L);
    }

    @DisplayName("지출 내역 수정 서비스 테스트 : 성공")
    @Test
    void updateExpenditure() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
        given(expenditureRepository.findById(anyLong())).willReturn(Optional.of(expenditure));

        //when
        ExpenditureUpdateRequestDto requestDto = new ExpenditureUpdateRequestDto(
            LocalDate.now().plusDays(2), 5000L, 2L, CategoryType.TRAFFIC, "지출을 줄이자");
        expenditureService.updateExpenditure(userId, expenditureId, requestDto);

        //then
        then(expenditureRepository).should(times(1)).findById(anyLong());
        assertThat(expenditure.getExpenditureDate()).isEqualTo(requestDto.getExpenditureDate());
        assertThat(expenditure.getAmount()).isEqualTo(requestDto.getAmount());
        assertThat(expenditure.getCategory().getId()).isEqualTo(requestDto.getCategoryId());
        assertThat(expenditure.getNote()).isEqualTo(requestDto.getNote());
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
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
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
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
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
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.of(expenditure));

        //when
        ExpenditureDetailsResponseDto responseDto = expenditureService.getExpenditureDetails(
            userId, expenditureId);

        //then
        then(expenditureRepository).should(times(1)).findByIdFetch(anyLong());
        assertThat(responseDto).isNotNull();
    }

    @DisplayName("현재 로그인한 사용자의 유효한 지출 내역 조회 테스트 with 카테고리 : 실패 - 접근 권한 없는 지출 내역")
    @Test
    void getValidExpenditureWithCategory() {
        //given
        String userId = "testUserId";
        Long expenditureId = 1L;
        Expenditure expenditure = Expenditure.builder().id(expenditureId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.of(expenditure));

        //when, then
        assertThatThrownBy(() -> expenditureService.getValidExpenditureWithCategory(
            "wrong " + userId, expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(FORBIDDEN_EXPENDITURE);
    }

    @DisplayName("해당 지출 내역 식별자로 조회 테스트 with 카테고리 : 실패 - 해당 지출 내역 존재하지 않음")
    @Test
    void getExpenditureWithCategory() {
        //given
        Long expenditureId = 1L;
        given(expenditureRepository.findByIdFetch(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> expenditureService.getExpenditureWithCategory(expenditureId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_EXPENDITURE);
    }
}
