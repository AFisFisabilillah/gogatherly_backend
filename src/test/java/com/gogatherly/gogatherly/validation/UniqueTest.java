package com.gogatherly.gogatherly.validation;

import com.gogatherly.gogatherly.dto.RegisterRequest;
import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.repository.EventRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class UniqueTest {
    @Autowired
    private Validator validator;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EventRepository eventRepository;

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

    @Test
    void fulltextSearch(){
        List<Event> run = eventRepository.fulltextSearch("Run");
        System.out.println(run.get(0).getTitle());
        Assertions.assertEquals(1, run.size());
    }
}
