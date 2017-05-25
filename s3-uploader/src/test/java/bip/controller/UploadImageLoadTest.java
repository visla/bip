package bip.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import bip.utilities.ImageResizeService;
import bip.utilities.ImageUploaderService;
import bip.utilities.NameUtil;
import bip.utilities.S3Uploader;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UploadImageLoadTest {
    
    @Autowired
    private MockMvc mockMvc;
        
    @Autowired
    private UploadController uploadController;
    
    @MockBean
    private S3Uploader s3Uploader;

    @MockBean
    private NameUtil nameUtils;
    
    private InputStream testImage;
    
    @Autowired
    CircuitBreakerRegistry registry;
        
    @Before
    public void setup() {
        when(nameUtils.makeKeyName(any(String.class))).thenReturn("mocked-key.jpg");
        when(nameUtils.updateKeyName("mocked-key.jpg", "LARGE")).thenReturn("mocked-key-LARGE.jpg");
        when(nameUtils.updateKeyName("mocked-key.jpg", "THUMB")).thenReturn("mocked-key-THUMB.jpg"); 
        
        testImage = this.getClass().getResourceAsStream("/test-photo.jpg");
        assertThat(testImage).isNotNull();
        
    }
    
    @Test
    public void testImageUploadParallel() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("image", "nicephoto.jpg", "image/jpeg", testImage);
        // always return true for upload.
        when(s3Uploader.upload(any(String.class), any(String[].class), any(String[].class)))
            .thenReturn(true);
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // submit 12 parallel image uploads. Anything more than 5 should trigger circuit breaker.
        final AtomicLong counter = new AtomicLong(0);
        for (int i = 0; i < 12; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        int expectedStatus = 200;
                        long running = counter.incrementAndGet();
                        if (running > 10) {
                            expectedStatus = 503;
                        }
                        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                            .file(imageFile))
                        .andExpect(status().is(expectedStatus));
                    } catch (Exception ex) {
                        // fail test if we get this exception.
                        assertThat(10).isEqualTo(0);
                    }
                }
            });
         }
    
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }
    
    @Test
    public void testImageUploadLoad() throws Exception { 
        MockMultipartFile imageFile = new MockMultipartFile("image", "nicephoto.jpg", "image/jpeg", testImage);
        CircuitBreaker breaker = registry.circuitBreaker("imageUpload");
        
        // Succeed 2 times
        when(s3Uploader.upload(any(String.class), any(String[].class), any(String[].class)))
                .thenReturn(true, true)
                .thenThrow(new Exception("fake fail"), new Exception("fake fail"), new Exception("fake fail"))
                .thenReturn(true, true, true);
        
        
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(200));
        
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(200));
        
        for(int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                    .file(imageFile))
                .andExpect(status().is(500));
        }
              
        // Our failure threshold went over limit of 50%
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(503));
        
        breaker.transitionToHalfOpenState();
        
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(200));

        // CLOSED state expected. so all good.
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(200));
        
    }
}