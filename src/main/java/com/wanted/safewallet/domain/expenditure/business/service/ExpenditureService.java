package com.wanted.safewallet.domain.expenditure.business.service;

import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureService {

    private final ExpenditureMapper expenditureMapper;
    private final CategoryService categoryService;
    private final ExpenditureRepository expenditureRepository;

    @Transactional
    public ExpenditureCreateResponseDto createExpenditure(String userId,
        ExpenditureCreateRequestDto requestDto) {
        validateRequest(requestDto);
        Expenditure expenditure = expenditureMapper.toEntity(userId, requestDto);
        Expenditure savedExpenditure = expenditureRepository.save(expenditure);
        return expenditureMapper.toDto(savedExpenditure);
    }

    private void validateRequest(ExpenditureCreateRequestDto requestDto) {
        CategoryValidRequestDto categoryValidDto = new CategoryValidRequestDto(
            requestDto.getCategoryId(), requestDto.getType());
        categoryService.validateCategory(categoryValidDto);
    }
}
