package com.wanted.safewallet.domain.expenditure.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.time.LocalDate;
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
            .category(Category.builder().id(1L).build())
            .expenditureDate(LocalDate.now()).amount(10000L).note("").build();
        given(expenditureRepository.save(any(Expenditure.class))).willReturn(expenditure);

        //when
        ExpenditureCreateRequestDto requestDto = new ExpenditureCreateRequestDto(
            LocalDate.now(), 10000L, 1L, CategoryType.FOOD, "");
        ExpenditureCreateResponseDto responseDto = expenditureService.createExpenditure(userId, requestDto);

        //then
        then(expenditureMapper).should(times(1)).toEntity(anyString(), any(ExpenditureCreateRequestDto.class));
        then(expenditureRepository).should(times(1)).save(any(Expenditure.class));
        then(expenditureMapper).should(times(1)).toDto(any(Expenditure.class));
        assertThat(responseDto.getExpenditureId()).isEqualTo(1L);
    }
}
