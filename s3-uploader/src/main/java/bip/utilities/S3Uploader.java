package bip.utilities;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class S3Uploader {
    static Logger logger = LogManager.getRootLogger();

    /*private String fileName;
    private String bucketName;
    private String keyName;*/

    public S3Uploader() {
    }

    public Boolean upload(String bucketName, String[] paths, String[] keys) throws Exception {
        AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
        try {
            for(int i = 0; i < paths.length; i++) {

                File file = new File(paths[i]);
                logger.info("Uploading image file to s3: " + paths[i] +
                    ", bucket:" + bucketName + ", key:" + keys[i]);
                s3client.putObject(new PutObjectRequest(
                                    bucketName, keys[i], file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            }
            
         } catch (AmazonServiceException ase) {
           logger.error("Failed uploading to S3.");
           logger.error("Error Message:" + ase.getMessage());
           logger.error("HTTP Status Code:" + ase.getStatusCode());
           throw ase;
        } catch (AmazonClientException ace) {
            logger.error("Failed communicating with AWS S3.");
            logger.error("Error Message:" + ace.toString());
            throw ace;
        }

        return true;
    }
}