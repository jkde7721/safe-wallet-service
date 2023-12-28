package com.wanted.safewallet.global.mail.service;

import static com.wanted.safewallet.global.exception.ErrorCode.MAIL_SEND_ERROR;

import com.wanted.safewallet.global.exception.BusinessException;
import com.wanted.safewallet.global.mail.dto.MailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final String endpoint;

    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
        @Value("${server-endpoint}") String endpoint) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.endpoint = endpoint;
    }

    @Async
    public void sendMail(MailMessage mailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessageHelper.setTo(mailMessage.getTo());
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            mimeMessageHelper.setText(renderText(mailMessage.getVariables(), mailMessage.getTemplate()), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BusinessException(MAIL_SEND_ERROR);
        }
    }

    //thymeleaf 통한 html 생성
    private String renderText(Map<String, Object> variables, String template) {
        Context context = new Context();
        context.setVariables(variables);
        context.setVariable("endpoint", endpoint);
        return templateEngine.process(template, context);
    }
}
