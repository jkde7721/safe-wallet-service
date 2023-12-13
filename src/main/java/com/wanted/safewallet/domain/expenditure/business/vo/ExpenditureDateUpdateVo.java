package com.wanted.safewallet.domain.expenditure.business.vo;

import java.time.LocalDateTime;

public record ExpenditureDateUpdateVo(LocalDateTime originalExpenditureDate,
                                      LocalDateTime updatedExpenditureDate) {

}
