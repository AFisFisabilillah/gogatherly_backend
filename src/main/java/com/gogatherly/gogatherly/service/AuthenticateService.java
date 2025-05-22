package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.*;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.ROLE;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
public class AuthenticateService {
    @Autowired
    private Validator validator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;


    @Transactional
    public User registerUser(RegisterRequest request){

        //Validasi
        Set<ConstraintViolation<RegisterRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }


        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setNik(request.getNik());
        user.setRole(ROLE.ROLE_USER);
        user.setVerificationCode(verificationCode());
        user.setVerificationExpired(LocalDateTime.now().plusMinutes(5L));
        user.setVerify(false);
        userRepository.save(user);
        sendVerificationEmail(user);

        return user;
    }

    @Transactional
    public User registerEventManager(RegisterRequest request){

        //Validasi
        Set<ConstraintViolation<RegisterRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }


        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setNik(request.getNik());
        user.setRole(ROLE.ROLE_EVENT_MANAGER);
        user.setVerificationCode(verificationCode());
        user.setVerificationExpired(LocalDateTime.now().plusMinutes(5L));
        user.setVerify(false);
        userRepository.save(user);
        sendVerificationEmail(user);

        return user;

    }

    private void sendVerificationEmail(User user){
        Context context = new Context();
        context.setVariable("code", user.getVerificationCode());
        String verify = templateEngine.process("verify", context);

        try{
            emailService.sendEmail(user.getEmail(), verify, "verification code");
        }catch (MessagingException e){
            System.out.println("gagal");
        }

    }

    public void verifyCode(VerificationCodeRequest request){
        Set<ConstraintViolation<VerificationCodeRequest>> validate = validator.validate(request);
        if(!validate.isEmpty()){
            throw new ConstraintViolationException(validate);
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "email Not found"));

        if(user.getVerificationExpired().isBefore(LocalDateTime.now())){
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "Verification code is expired");
        }

        if(!Objects.equals(user.getVerificationCode(), request.getCode())){
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "Verification code is wrong");
        }

        user.setVerify(true);
        user.setVerificationCode(null);
        userRepository.save(user);

    }

    public void resendEmailVerification(ResendEmailVerificationRequest request){
        Set<ConstraintViolation<ResendEmailVerificationRequest>> validate = validator.validate(request);
        if(!validate.isEmpty()){
            throw new ConstraintViolationException(validate);
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "email Not found"));
        if(user.getVerify()){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "your akun is verify, you can't send email verification");
        }

        user.setVerificationCode(verificationCode());
        user.setVerificationExpired(LocalDateTime.now().plusMinutes(5));
        sendVerificationEmail(user);
        userRepository.save(user);

    }

    public void changeEmail(ChangeEmailVerificationRequest request){
        Set<ConstraintViolation<ChangeEmailVerificationRequest>> validate = validator.validate(request);
        if(!validate.isEmpty()){
            throw new ConstraintViolationException(validate);
        }

        User user = userRepository.findByEmail(request.getOldEmail()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "email Not found"));

        if(user.getVerify()){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "your akun is verify, you can't send email verification");
        }

        user.setEmail(request.getNewEmail());

        user.setVerificationCode(verificationCode());
        user.setVerificationExpired(LocalDateTime.now().plusMinutes(5));
        sendVerificationEmail(user);
        userRepository.save(user);


    }

    public LoginResponse  login(LoginRequest request){
        Set<ConstraintViolation<LoginRequest>> validate = validator.validate(request);
        if(!validate.isEmpty()){
            throw new ConstraintViolationException(validate);
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "login failed because email not found"));
        if(!user.getVerify()){
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "login failed because emailnot verify");
        }
        try{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            log.info("Aapakah logi berhasil ? "+authenticate.isAuthenticated());
            if(!authenticate.isAuthenticated()){
                throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "login failed because email or password wrong ");
            }

            String token = jwtService.buildToken(new HashMap<>(), user);

            return LoginResponse
                    .builder()
                    .id(user.getId())
                    .token(token)
                    .name(user.getName())
                    .build();

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED, "error", "login failed because email or password wrong ");
        }
    }


    private int verificationCode(){
        return new Random().nextInt(900000)+100000;
    }

}
