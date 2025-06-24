package com.gogatherly.gogatherly.service;

import com.github.aytchell.qrgen.QrGenerator;
import com.github.aytchell.qrgen.colors.RgbValue;
import com.github.aytchell.qrgen.config.ErrorCorrectionLevel;
import com.github.aytchell.qrgen.config.ImageFileType;
import com.github.aytchell.qrgen.config.MarkerStyle;
import com.github.aytchell.qrgen.config.PixelStyle;
import com.github.aytchell.qrgen.exceptions.QrConfigurationException;
import com.github.aytchell.qrgen.exceptions.QrGenerationException;
import com.gogatherly.gogatherly.dto.VerifyQrCodeRequest;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.entity.TicketInstance;
import com.gogatherly.gogatherly.model.entity.User;
import com.gogatherly.gogatherly.model.repository.EventRepository;
import com.gogatherly.gogatherly.model.repository.TicketInstanceRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class QrCodeService {

    @Autowired
    private Validator validator;

    private QrGenerator generator = new QrGenerator();
    @Autowired
    private TicketInstanceRepository ticketInstanceRepository;
    @Autowired
    private EventRepository eventRepository;

    public String createQrCode(String data) throws QrConfigurationException, IOException, QrGenerationException {
        Path path = generator
                .withMarkerStyle(MarkerStyle.ROUND_CORNERS)
                .withPixelStyle(PixelStyle.DOTS)
                .withColors(
                        new RgbValue(79, 35, 173),
                        new RgbValue(255,255,255),
                        new RgbValue(127 ,91 ,229),
                        new RgbValue(105 ,65 ,198)
                )
                .withMargin(2)
                .withSize(1000, 1000)
                .as(ImageFileType.PNG)
                .withLogo(Path.of("./upload/logo/gogatherly.png"))
                .withErrorCorrection(ErrorCorrectionLevel.H)
                .writeToTmpFile(data);
        System.out.println("path : "+path.toAbsolutePath());

        String newFileName = "qrcode_" + data + ".png";
        Path res = Files.copy(path, Path.of("./upload/public/ticket_qrcode/"+newFileName));

        return newFileName;
    }

    public  TicketInstance scanQrcode(VerifyQrCodeRequest request,Integer eventId){
        Set<ConstraintViolation<VerifyQrCodeRequest>> validate = validator.validate(request);
        if(validate.size() > 0 ){
            throw new ConstraintViolationException(validate);
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Event event = eventRepository.findByIdAndUser_Id(eventId, user.getId()).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "event ID not found"));


        TicketInstance ticket = ticketInstanceRepository.findByIdAndTicket_Event(request.getTicketId(), event).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "invalid qrcode ticket id "));

        if(ticket.getUsed()){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "ticket is used");
        }

        if(!ticket.getUser().getNik().equals(request.getNik())){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error","Please double-check the NIK or ensure the ticket is used by its rightful owner.");
        }

        ticket.setUsed(true);
        ticket.setUsedAt(LocalDateTime.now());
        ticketInstanceRepository.save(ticket);

        return ticket;
    }
}
