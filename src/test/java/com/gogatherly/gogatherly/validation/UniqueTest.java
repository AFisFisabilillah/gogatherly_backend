package com.gogatherly.gogatherly.validation;

import com.gogatherly.gogatherly.dto.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Set;

@SpringBootTest
public class UniqueTest {
    @Autowired
    private Validator validator;

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void testValidation(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("afis");
        registerRequest.setEmail("afis@gmail.com");
        Set<ConstraintViolation<RegisterRequest>> validate = validator.validate(registerRequest);
        if(validate.size() > 0){
            validate.forEach(constraint ->{
                System.out.println(constraint.getPropertyPath() + " : "+constraint.getMessage());
            });
        }
    }

    @Test
    void emailTest(){
        Assertions.assertNotNull(javaMailSender);
    }
}
