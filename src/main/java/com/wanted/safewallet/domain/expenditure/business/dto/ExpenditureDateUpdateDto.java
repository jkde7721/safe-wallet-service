package com.wanted.safewallet.domain.expenditure.business.dto;

import java.time.LocalDateTime;

public record ExpenditureDateUpdateDto(LocalDateTime originalExpenditureDate,
                                       LocalDateTime updatedExpenditureDate) {

}
