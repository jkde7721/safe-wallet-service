package com.wanted.safewallet.domain.expenditure.web.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureDetailsResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = STRING, timezone = "Asia/Seoul")
    private LocalDateTime expenditureDate;

    private Long amount;

    private Long categoryId;

    private CategoryType type;

    private String title;

    private String note;

    private List<String> imageUrls;
}
