package bip.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import bip.utilities.NameUtil;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UploadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
        
    @Autowired
    private UploadController uploadController;

    @MockBean
    private NameUtil nameUtils;
    
    private InputStream testImage;
    
    @Before
    public void setup() {
        when(nameUtils.makeKeyName(any(String.class))).thenReturn("mocked-key.jpg");
        testImage = this.getClass().getResourceAsStream("/test-photo.jpg");
        assertThat(testImage).isNotNull();
        
    }
    
    @Test
    public void testRegularImageUpload() throws Exception { 
        assertThat(testImage).isNotNull();
        MockMultipartFile imageFile = new MockMultipartFile("image", "nicephoto.jpg", "image/jpeg", testImage);
        
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("imageUrl", "https://s3-us-west-2.amazonaws.com/bip-test/mocked-key.jpg");
        String expectedResult = jsonResult.toString();
        
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(200))
            .andExpect(content().json(expectedResult));
    }
    
    @Test
    public void testMissingImageUpload() throws Exception {
        assertThat(testImage).isNotNull();
        MockMultipartFile imageFile = new MockMultipartFile("file", "nicephoto.jpg", "image/jpeg", testImage);
        
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(500));
    }
    
    @Test
    public void testImageUploadWrongType() throws Exception {
        assertThat(testImage).isNotNull();
        MockMultipartFile imageFile = new MockMultipartFile("image", "nicephoto.png", "image/png", testImage);
        
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/image/upload")
                .file(imageFile))
            .andExpect(status().is(400));
    }

}