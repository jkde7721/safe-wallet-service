package com.wanted.safewallet.domain.expenditure.business.mapper;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ExpenditureMapper {

    public Expenditure toEntity(String userId, ExpenditureCreateRequestDto requestDto) {
        return Expenditure.builder()
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(requestDto.getCategoryId()).build())
            .expenditureDate(requestDto.getExpenditureDate())
            .amount(requestDto.getAmount())
            .note(requestDto.getNote()).build();
    }

    public ExpenditureCreateResponseDto toCreateDto(Expenditure expenditure) {
        return new ExpenditureCreateResponseDto(expenditure.getId());
    }
}
