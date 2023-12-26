package com.wanted.safewallet.global.mail.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MailMessage {

    private String to; //수신자
    private String subject; //메일 제목
    private Map<String, Object> variables; //메일 내용 (thymeleaf 변수)
    private String template; //템플릿 이름
}
