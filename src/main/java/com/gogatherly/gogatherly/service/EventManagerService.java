package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.EventManagerResponse;
import com.gogatherly.gogatherly.dto.EventManagerUpdateRequest;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.EventManager;
import com.gogatherly.gogatherly.model.repository.EventManagerRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class EventManagerService {
    @Autowired
    private EventManagerRepository eventManagerRepository;

    @Autowired
    private Validator validator;

    public EventManagerResponse getProfile(){
        EventManager eventManager = (EventManager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EventManagerResponse response = new EventManagerResponse();
        response.setName(eventManager.getName());
        response.setPhotoProfile("/public/profilePhoto/"+eventManager.getProfilePhoto());
        response.setEmail(eventManager.getEmail());
        response.setId(eventManager.getId());
        response.setDescription(eventManager.getDescription());
        return response;
    }

    public EventManagerResponse updateProfile(EventManagerUpdateRequest request){
        Set<ConstraintViolation<EventManagerUpdateRequest>> validate = validator.validate(request);
        if(validate.size() > 0 ){
            throw new ConstraintViolationException(validate);
        }
        EventManager eventManager = (EventManager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MultipartFile photo = request.getPhoto();

//        Mengecek apkaah file kosong ata tidak
        if(!(photo == null || photo.isEmpty())){
            log.info("ada file ");
            if(photo.getContentType().startsWith("image")){
                log.info("ada file image");
//                Menghapus file lama
                Path deletePath = Path.of("upload/public/profilePhoto/" + eventManager.getProfilePhoto());
                if(Files.exists(deletePath)){
                    log.info("ada file ");
                    try{
                        Files.delete(deletePath);
                        log.info("berhasil di hapus");
                    }catch (IOException e){
                        log.error(e.toString());
                    }
                }

                String extension = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString()+eventManager.getName().trim().replace(" ", "_")+extension;

                Path uploadPath = Path.of("upload/public/profilePhoto/" + newFileName);

                try{
                    Files.write(uploadPath, photo.getBytes());
                    log.info("berhasil di hapus");
                }catch (IOException e){
                    log.error(e.toString());
                }
                eventManager.setProfilePhoto(newFileName);
            }else{

                throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "erorr","your file not an image", new HashMap<>(Map.of("photo", "is't  an image")));
            }
        }

        if(eventManager != null && !request.getDescription().isBlank()){
            eventManager.setDescription(request.getDescription());
        }
        eventManager.setName(request.getName());

        eventManagerRepository.save(eventManager);

        EventManagerResponse response = new EventManagerResponse();
        response.setName(eventManager.getName());
        response.setPhotoProfile("/public/profilePhoto/"+eventManager.getProfilePhoto());
        response.setEmail(eventManager.getEmail());
        response.setId(eventManager.getId());
        response.setDescription(eventManager.getDescription());

        return response;

    }
}
