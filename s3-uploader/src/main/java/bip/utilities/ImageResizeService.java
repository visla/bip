package bip.utilities;

import org.springframework.web.client.RestTemplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;
import java.net.URI;

@Service
public class ImageResizeService {
    static Logger logger = LogManager.getRootLogger();

    private final RestTemplate restTemplate;

    public ImageResizeService(RestTemplate rest) {
        this.restTemplate = rest;
    }

    public String resizeImage(String fileName, String imageSize) {
        URI uri = URI.create("http://bip-resizer/image/resize?filename=" + fileName + "&image_size=" + imageSize);
        return this.restTemplate.getForObject(uri, String.class);
    }
}

