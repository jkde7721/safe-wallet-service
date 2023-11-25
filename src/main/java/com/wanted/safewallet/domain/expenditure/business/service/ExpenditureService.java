package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_EXPENDITURE;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_EXPENDITURE;

import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListResponseDto;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureService {

    private final ExpenditureMapper expenditureMapper;
    private final CategoryService categoryService;
    private final ExpenditureRepository expenditureRepository;

    public ExpenditureDetailsResponseDto getExpenditureDetails(String userId, Long expenditureId) {
        Expenditure expenditure = getValidExpenditureWithCategory(userId, expenditureId);
        return expenditureMapper.toDetailsDto(expenditure);
    }

    public ExpenditureListResponseDto getExpenditureList(String userId,
        ExpenditureSearchCond searchCond, Pageable pageable) {
        List<StatsByCategoryResponseDto> statsByCategory = expenditureRepository.getStatsByCategory(userId, searchCond);
        Page<Expenditure> expenditurePage = expenditureRepository.findAllFetch(userId, searchCond, pageable);
        return expenditureMapper.toListDto(statsByCategory, expenditurePage);
    }

    @Transactional
    public ExpenditureCreateResponseDto createExpenditure(String userId,
        ExpenditureCreateRequestDto requestDto) {
        validateRequest(requestDto);
        Expenditure expenditure = expenditureMapper.toEntity(userId, requestDto);
        Expenditure savedExpenditure = expenditureRepository.save(expenditure);
        return expenditureMapper.toCreateDto(savedExpenditure);
    }

    @Transactional
    public void updateExpenditure(String userId, Long expenditureId, ExpenditureUpdateRequestDto requestDto) {
        validateRequest(requestDto);
        Expenditure expenditure = getValidExpenditure(userId, expenditureId);
        expenditure.update(requestDto.getCategoryId(), requestDto.getExpenditureDate(),
            requestDto.getAmount(), requestDto.getNote());
    }

    @Transactional
    public void deleteExpenditure(String userId, Long expenditureId) {
        Expenditure expenditure = getValidExpenditure(userId, expenditureId);
        expenditure.softDelete();
    }

    public Expenditure getValidExpenditure(String userId, Long expenditureId) {
        Expenditure expenditure = getExpenditure(expenditureId);
        if (Objects.equals(expenditure.getUser().getId(), userId)) {
            return expenditure;
        }
        throw new BusinessException(FORBIDDEN_EXPENDITURE);
    }

    public Expenditure getValidExpenditureWithCategory(String userId, Long expenditureId) {
        Expenditure expenditure = getExpenditureWithCategory(expenditureId);
        if (Objects.equals(expenditure.getUser().getId(), userId)) {
            return expenditure;
        }
        throw new BusinessException(FORBIDDEN_EXPENDITURE);
    }

    public Expenditure getExpenditure(Long expenditureId) {
        return expenditureRepository.findById(expenditureId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_EXPENDITURE));
    }

    public Expenditure getExpenditureWithCategory(Long expenditureId) {
        return expenditureRepository.findByIdFetch(expenditureId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_EXPENDITURE));
    }

    private void validateRequest(ExpenditureCreateRequestDto requestDto) {
        CategoryValidRequestDto categoryValidDto = new CategoryValidRequestDto(
            requestDto.getCategoryId(), requestDto.getType());
        categoryService.validateCategory(categoryValidDto);
    }

    private void validateRequest(ExpenditureUpdateRequestDto requestDto) {
        CategoryValidRequestDto categoryValidDto = new CategoryValidRequestDto(
            requestDto.getCategoryId(), requestDto.getType());
        categoryService.validateCategory(categoryValidDto);
    }
}
