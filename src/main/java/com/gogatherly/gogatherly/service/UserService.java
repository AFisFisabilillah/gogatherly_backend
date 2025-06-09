package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.EventManagerResponse;
import com.gogatherly.gogatherly.dto.UserResponse;
import com.gogatherly.gogatherly.dto.UserUpdateRequest;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.EventManager;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.EventManagerRepository;
import com.gogatherly.gogatherly.model.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private EventManagerRepository eventManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElse(null);
        log.info("berhasi; menemukan user degan nama"+user.getName());
        return user;
    }

    public User changePhotoProfile(MultipartFile photoProfile){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!photoProfile.isEmpty()){
            log.info(photoProfile.getContentType());
            if(photoProfile.getContentType().startsWith("image")){
                if(user.getProfilePhoto() != null){
                    try {
                        Path deletePath = Path.of("upload/public/profilePhoto/" + user.getProfilePhoto());
                        if(Files.exists(deletePath)){
                            Files.delete(deletePath);
                        }
                    }catch (IOException e){
                        throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", e.getMessage());
                    }
                }
                String extension = photoProfile.getOriginalFilename().substring(photoProfile.getOriginalFilename().lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString()+"_"+user.getName().replace(" ", "_")+extension;

                try {
                    Path uploadPath = Path.of("upload/public/profilePhoto/" + newFileName);
                    Files.write(uploadPath, photoProfile.getBytes());
                }catch (IOException e){
                    System.err.println(e);
                    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", e.getMessage());
                }

                user.setProfilePhoto(newFileName);
                userRepository.save(user);
            }else{
                Map<String , String> errors = new HashMap<>();
                errors.put("photoProfile", "this field not an image");
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "your file not an image");
            }
        }else{
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "photoProfile not empty");
        }



        return user;
    }

    public UserResponse getProfile(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse response = new UserResponse();
        response.setPhotoProfile("/public/profilePhoto/"+user.getProfilePhoto());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setNik(user.getNik());
        response.setPhoneNumber(user.getPhoneNumber());
        return response;
    }
    
    public UserResponse updateProfile(UserUpdateRequest request){
        Set<ConstraintViolation<UserUpdateRequest>> validate = validator.validate(request);
        if(validate.size() > 0){
            throw new ConstraintViolationException(validate);
        }

        MultipartFile photoProfile = request.getPhotoProfile();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(photoProfile != null ){
            log.info("tipe image : "+photoProfile.getContentType());
            log.info("startswit image : "+(photoProfile.getContentType().startsWith("image")));

            if(!photoProfile.isEmpty()){
                if(photoProfile.getContentType().startsWith("image")){
                    if(user.getProfilePhoto() != null){
                        try {
                            Path deletePath = Path.of("upload/public/profilePhoto/" + user.getProfilePhoto());
                            if(Files.exists(deletePath)){
                                Files.delete(deletePath);
                            }
                        }catch (IOException e){
                            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", e.getMessage());
                        }
                    }
                    String extension = photoProfile.getOriginalFilename().substring(photoProfile.getOriginalFilename().lastIndexOf("."));
                    String newFileName = UUID.randomUUID().toString()+"_"+user.getName().trim()+extension;

                    try {
                        Path uploadPath = Path.of("upload/public/profilePhoto/" + newFileName);
                        Files.write(uploadPath, photoProfile.getBytes());
                    }catch (IOException e){
                        System.err.println(e);
                        throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", e.getMessage());
                    }
                    user.setProfilePhoto(newFileName);
                }else{
                    Map<String , String> errors = new HashMap<>();
                    errors.put("photoProfile", "this field not an image");
                    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "your file not an image");
                }
            }else{
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "your file not empty");
            }
        }

        user.setName(request.getName());
        if(!request.getNumberPhone().equals(user.getPhoneNumber())){
            if(userRepository.existsByPhoneNumber(request.getNumberPhone())){
                Map<String, String> errors = new HashMap<>(Map.of("numberPhone", "is Alredy exist"));
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST,"error", "number phone alredy exist", errors);
            }else{
                user.setPhoneNumber(request.getNumberPhone());
            }
        }
        userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setPhotoProfile("/public/profilePhoto/"+user.getProfilePhoto());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());

        return response;
    }
    
    public void getEventManager(){
        List<EventManager> eventManagers = eventManagerRepository.findAll();
    }
}
