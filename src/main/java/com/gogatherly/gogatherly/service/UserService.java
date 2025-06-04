package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.EventManagerResponse;
import com.gogatherly.gogatherly.dto.UserResponse;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.UserRepository;
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
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElse(null);
        log.info("berhasi; menemukan user degan nama"+user.getName());
        return user;
    }

    public User changePhotoProfile(MultipartFile photoProfile){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.getProfilePhoto() != null){
            try {
                Path deletePath = Path.of("upload/public/banner/profilePhoto/" + user.getProfilePhoto());
                if(Files.exists(deletePath)){
                    Files.delete(deletePath);
                }
            }catch (IOException e){
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", e.getMessage());
            }
        }
        String extension = photoProfile.getOriginalFilename().substring(photoProfile.getOriginalFilename().lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString()+"_"+user.getName()+extension;

        try {
            Path uploadPath = Path.of("upload/public/banner/profilePhoto" + newFileName);
            Files.write(uploadPath, photoProfile.getBytes());
        }catch (IOException e){
            System.err.println(e);
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", e.getMessage());
        }

        user.setProfilePhoto(newFileName);
        userRepository.save(user);

        return user;
    }

    public UserResponse getProfile(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse response = new UserResponse();
        response.setPhotoProfile(user.getProfilePhoto());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setNik(response.getNik());
        response.setPhoneNumber(user.getPhoneNumber());
        return response;
    }
}
