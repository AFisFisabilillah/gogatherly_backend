package com.gogatherly.gogatherly;

import com.github.aytchell.qrgen.QrGenerator;
import com.github.aytchell.qrgen.colors.RgbValue;
import com.github.aytchell.qrgen.colors.RgbaValue;
import com.github.aytchell.qrgen.config.ErrorCorrectionLevel;
import com.github.aytchell.qrgen.config.MarkerStyle;
import com.github.aytchell.qrgen.config.PixelStyle;
import com.github.aytchell.qrgen.exceptions.QrConfigurationException;
import com.github.aytchell.qrgen.exceptions.QrGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.TestExecutionListeners;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class QrCodeTest {

    @Test
    public void createQrCode() throws QrGenerationException, IOException, InterruptedException, QrConfigurationException {
        QrGenerator generator = new QrGenerator();

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
                .withLogo(Path.of("./upload/logo/gogatherly_logo.jpg"))
                .withErrorCorrection(ErrorCorrectionLevel.H)
                .writeToTmpFile("afisifsabilliah");
        System.out.println("path : "+path.toAbsolutePath());

        Files.copy(path, Path.of("./upload/public/"+ UUID.randomUUID().toString()+".png"));

    }
}
