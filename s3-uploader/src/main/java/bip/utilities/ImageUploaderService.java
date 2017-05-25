package bip.utilities;

import bip.error.ServiceNotAvailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@CircuitBreaker(backend = "imageUpload")
@Service
public class ImageUploaderService {
    static Logger logger = LogManager.getRootLogger();
    
    @Autowired
    private S3Uploader s3Uploader;
    
    @Autowired
    private CircuitBreakerRegistry registry;
    
    private int maxParallelUploads = 10;
    
    public ImageUploaderService() {
        // Setup max requests for upload to s3 to come from ENVVAR
        try {
            this.maxParallelUploads = Integer.parseInt(System.getenv("MAX_PARALLEL_UPLOADS"));
        } catch (NumberFormatException ex) {
            this.maxParallelUploads = 10;
        }
    }

    public Boolean uploadToS3(String bucketName, String[] paths, String[] keys) throws Exception {
        // Make sure we check max number of requests to this service.
        io.github.resilience4j.circuitbreaker.CircuitBreaker breaker = registry.circuitBreaker("imageUpload");
        if (breaker.getMetrics().getNumberOfBufferedCalls() > maxParallelUploads) {
            throw new ServiceNotAvailableException("Too many calls");
        }
        
        s3Uploader.upload(bucketName, paths, keys);
        return true;
    }
}