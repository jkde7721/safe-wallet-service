package com.wanted.safewallet.config;

import com.wanted.safewallet.domain.auth.utils.JwtProperties;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class) //load application.yml
@EnableConfigurationProperties(value = JwtProperties.class)
@ExtendWith(SpringExtension.class)
public @interface JwtPropertiesConfiguration {

}
