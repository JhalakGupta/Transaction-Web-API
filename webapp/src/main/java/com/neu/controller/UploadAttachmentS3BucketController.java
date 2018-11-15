package com.neu.controller;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.neu.pojo.TransactionDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class UploadAttachmentS3BucketController {



//    @Autowired
//    private Environment environment;
@Value("${amazonProperties.bucketName}")
private String bucketN;

    ObjectMetadata objectMetadata = new ObjectMetadata();

    public String uploadFileOnS3(TransactionDetails transactionDetail, MultipartFile multipartfile) {

        //String bucketName = System.getProperty("bucket.name");
        //System.out.println("bucket name is :" + System.getProperty("bucket.name"));


        System.out.println("In function UploadFile");

       InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider
               (true);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(Regions.US_EAST_1).build();
        System.out.println("InstanceProfileCreated");
        String bucketName = null;


        System.out.println("Before the Bucket List for loop");
        
        List<Bucket> buckets = s3Client.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println("In the bucket list loop");
            System.out.println(bucket.getName());
            if(bucket.getName().contains("csye6225") && !bucket.getName().contains("code-deploy") && !bucket.getName().contains("lambda")){
                bucketName = bucket.getName();
                break;
            }

        }
        try {

            System.out.println("Uploading file to s3 bucket" + bucketName);
            objectMetadata.setContentType(multipartfile.getContentType());
            //File filename = convertFromMultipart(multipartfile);
            System.out.println(transactionDetail.getId()+"\n"+multipartfile.getOriginalFilename());
            s3Client.putObject(new PutObjectRequest(bucketName, transactionDetail.getTransactionDetailsId().toString() + "/" +
                    multipartfile.getOriginalFilename(), multipartfile.getInputStream(), objectMetadata));
            return transactionDetail.getTransactionDetailsId().toString() + multipartfile.getOriginalFilename();
        } catch (AmazonServiceException ase) {
            System.out.println("bucket name: " + bucketName);
            System.out.println("Request made to s3 bucket failed");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return null;

        } catch (Exception e) {
            System.out.println("----------Stack Trace------\n" + e.getStackTrace());
            return null;
        }

    }

    /**
     * This method converts a multipart file to File format
     *
     * @param file : Transaction Attachment
     */
    public File convertFromMultipart(MultipartFile file) throws Exception {
        File convFile = new File(file.getOriginalFilename());
        System.out.println("jhalak");
        //convFile.createNewFile();
        objectMetadata.setContentType(file.getContentType());
        System.out.println("janhavi");
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
