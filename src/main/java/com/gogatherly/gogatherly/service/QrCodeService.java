package com.gogatherly.gogatherly.service;

import com.github.aytchell.qrgen.QrGenerator;
import com.github.aytchell.qrgen.colors.RgbValue;
import com.github.aytchell.qrgen.config.ErrorCorrectionLevel;
import com.github.aytchell.qrgen.config.ImageFileType;
import com.github.aytchell.qrgen.config.MarkerStyle;
import com.github.aytchell.qrgen.config.PixelStyle;
import com.github.aytchell.qrgen.exceptions.QrConfigurationException;
import com.github.aytchell.qrgen.exceptions.QrGenerationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
public class QrCodeService {
    private QrGenerator generator = new QrGenerator();

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
                .withMargin(3)
                .withSize(1000, 1000)
                .as(ImageFileType.PNG)
                .withLogo(Path.of("./upload/logo/gogatherly_logo.jpg"))
                .withErrorCorrection(ErrorCorrectionLevel.H)
                .writeToTmpFile(data);
        System.out.println("path : "+path.toAbsolutePath());

        String newFileName = "qrcode_" + data + ".png";
        Path res = Files.copy(path, Path.of("./upload/public/ticket_qrcode/"+newFileName));

        return newFileName;
    }

    public  void scanQrcode(){

    }
}
