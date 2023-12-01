package com.wanted.safewallet.docs.common.response;

import static org.springframework.http.HttpStatus.*;

import com.wanted.safewallet.global.dto.response.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commonResponse")
public class CommonResponseController {

    @GetMapping
    public CommonResponse<CommonResponseData> getCommonResponse() {
        return new CommonResponse<>(OK.value(), OK.name(), "요청이 정상 처리되었습니다.",
            new CommonResponseData("Eden", 25));
    }

    @Getter
    @AllArgsConstructor
    static class CommonResponseData {

        private String name;
        private Integer age;
    }
}
