package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.ScannerRequest;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.ROLE;
import com.gogatherly.gogatherly.model.entity.Scanner;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.EventRepository;
import com.gogatherly.gogatherly.model.repository.ScannerRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ScannerService {
    @Autowired
    private Validator validator;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ScannerRepository scannerRepository;

    public Scanner createScanner(ScannerRequest request, Integer eventId){
        Set<ConstraintViolation<ScannerRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found event id"));

        Scanner scanner = new Scanner();
        scanner.setRole(ROLE.SCANNER);
        scanner.setEvent(event);
        scanner.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        scanner.setUsername(request.getUsername());

        scannerRepository.save(scanner);

        return scanner;

    }
}
