package com.wanted.safewallet.global.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private final String timestamp = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final int status;
    private final String code;
    private final String message;
    private final T data;
}
