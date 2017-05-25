package bip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import bip.utilities.ImageResizeService;
import bip.utilities.ImageSizes;
import bip.utilities.ImageUploaderService;
import bip.utilities.NameUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import org.springframework.http.MediaType;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import bip.Main;
import bip.error.ServiceNotAvailableException;
import bip.utilities.ImageResizeResult;
import org.springframework.web.client.RestTemplate;

@RestController
public class UploadController {
    static Logger logger = LogManager.getRootLogger();

    private CircuitBreaker circuitBreaker;

    public UploadController() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(30000))
                .ringBufferSizeInHalfOpenState(1)
                .ringBufferSizeInClosedState(5)
                .build();

        circuitBreaker = CircuitBreaker.of("imageUploader", circuitBreakerConfig);
    }

    /**
     * Store files here.
     */
    private static String UPLOADED_FOLDER = "/uploaded/";

    @Autowired
    private ImageResizeService imageResizeService;

    @Autowired
    private NameUtil nameUtil;

    @Autowired
    private ImageUploaderService imageUploaderService;

    @Bean
    public RestTemplate rest(RestTemplateBuilder builder) {
        return builder.build();
    }

    private Boolean isImageAcceptable(String imageFileName) {
        String extension = FilenameUtils.getExtension(imageFileName).toLowerCase();
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handle image uploads.
     * @param  file      [description]
     * @return           [description]
     * @throws Exception [description]
     */
    @PostMapping(value = "/image/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody UploadResult imageUpload(
            @RequestParam("image") MultipartFile image, 
            @RequestParam(value="imageSize", required = false) String imageSize) throws Exception {
        logger.info("image upload called");
        
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("image file should be submitted");
        }
        
        if(imageSize == null) {
            imageSize = "ORIGINAL";
        }
        
        if (isImageAcceptable(image.getOriginalFilename()) == false) {
            throw new IllegalArgumentException("only jpg images are accepted");
        }

        String bucketName = System.getenv("S3_BUCKET");
        String bucketUrl = System.getenv("S3_BUCKET_URL");
        if (bucketName == null) {
            throw new Exception("bucket is not defined");
        }

        String keyName = this.nameUtil.makeKeyName(image.getOriginalFilename());
        
        // Get the file and save it in tmp.
        byte[] bytes = image.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + keyName);
        String filePath = path.toString();
        Files.write(path, bytes);

        // Resize Image if not ORIGINAL as size.
        if (!imageSize.equals(ImageSizes.ORIGINAL.toString())) {
            logger.info("Resizing image to size " + imageSize);
            String result = this.imageResizeService.resizeImage(path.toString(), imageSize);
            JSONObject resultJson = new JSONObject(result);
            logger.info("Resize complete:" + resultJson.get("resized_path").toString());
            filePath = resultJson.get("resized_path").toString();
        }
        
        Boolean uploadResult = imageUploaderService.uploadToS3(bucketName, 
                (new String[] { filePath }), 
                (new String[] { keyName }));
        
        if (!uploadResult) {
            throw new ServiceNotAvailableException("S3 Service not available");
        }

        return new UploadResult(bucketUrl + keyName);
    }

}