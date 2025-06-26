package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.ScannerLoginRequest;
import com.gogatherly.gogatherly.dto.ScannerRequest;
import com.gogatherly.gogatherly.dto.ScannerUdateRequest;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    public Scanner createScanner(ScannerRequest request, Integer eventId){
        Set<ConstraintViolation<ScannerRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found event id"));

        Scanner scanner = new Scanner();
        scanner.setRole(ROLE.ROLE_SCANNER);
        scanner.setEvent(event);
        scanner.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        scanner.setUsername(request.getUsername());

        scannerRepository.save(scanner);

        return scanner;

    }

    public Scanner changePassword(ScannerUdateRequest request, Integer scannerId, Integer eventId){
        Set<ConstraintViolation<ScannerUdateRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found event id"));
        Scanner scanner = scannerRepository.findByEventAndId(event, scannerId).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found scanner with id " + scannerId));
        scanner.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        scannerRepository.save(scanner);
        return scanner;
    }

    public Scanner deletedScanner(Integer eventId, Integer scannerId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found event id"));

        Scanner scanner = scannerRepository.findByEventAndId(event, scannerId).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found scanner with id " + scannerId));
        scannerRepository.delete(scanner);
        return scanner;
    }

    public List<Scanner> getAllScanner(Integer eventId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "not found event id"));

        List<Scanner> scanners = scannerRepository.findByEvent(event);
        return scanners;
    }

    public Map<String , Object> loginScanner(ScannerLoginRequest request){
        Set<ConstraintViolation<ScannerLoginRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }

        Scanner scanner = scannerRepository.findByUsername(request.getUsername()).orElseThrow(() -> new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "username or password worng"));

        try{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            if(!authenticate.isAuthenticated()){
                throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "login failed because email or password wrong ");
            }
            LinkedHashMap<String, Object> claims = new LinkedHashMap<>();
            claims.put("id", scanner.getId());
            claims.put("username", scanner.getUsername());
            String token = jwtService.generateToken(claims, scanner);

            LinkedHashMap<String, Object> resonses = new LinkedHashMap<>();

            claims.put("id", scanner.getId());
            claims.put("username", scanner.getUsername());
            claims.put("token", token);

            return claims;
        }catch (AuthenticationException e){
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "login failed because email or password wrong ");
        }
    }

}
