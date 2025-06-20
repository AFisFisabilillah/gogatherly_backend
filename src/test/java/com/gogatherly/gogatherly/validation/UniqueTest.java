package com.gogatherly.gogatherly.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.*;

@SpringBootTest
public class UniqueTest {
    @Autowired
    private Validator validator;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
    void fulltextSearch() throws JsonProcessingException {
        UUID idRand = UUID.randomUUID();
        Map<String, Object> params = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", "265000");

        Map<String, String> creditCard = new HashMap<>();
        creditCard.put("secure", "true");

        params.put("transaction_details", transactionDetails);
        params.put("credit_card", creditCard);

        String json = objectMapper.writeValueAsString(params);

        System.out.println(json);
    }
}
