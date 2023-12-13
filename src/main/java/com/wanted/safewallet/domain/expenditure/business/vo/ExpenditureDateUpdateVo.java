package com.wanted.safewallet.domain.expenditure.business.vo;

import java.time.LocalDate;

public record ExpenditureDateUpdateVo(LocalDate originalExpenditureDate,
                                      LocalDate updatedExpenditureDate) {

}
