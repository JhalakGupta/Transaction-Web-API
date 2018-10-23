package com.neu.controller;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.neu.pojo.TransactionDetails;
import org.springframework.context.annotation.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class UploadAttachmentS3BucketController {




    public String uploadFileOnS3(TransactionDetails transactionDetail, MultipartFile multipartfile){

        //String bucketName = System.getProperty("bucket.name");
        //System.out.println("bucket name is :" + System.getProperty("bucket.name"));

        /*Assigns Temporary credentials to IAM role
         * InstanceProfileCredentialsProvider : false does not refresh the credentials
         */
      //  AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
               // .withCredentials(new InstanceProfileCredentialsProvider(false))
               // .build();

        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();

        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                //.withRegion(Regions.US_EAST_2)
                .build();

        String bucketName=null;

        List<Bucket> buckets = s3Client.listBuckets();
        for(Bucket bucket : buckets) {
            System.out.println(bucket.getName());

            if(bucket.getName().contains("csye6225"))
            {
                bucketName=bucket.getName();
               break;
            }

        }
        try{

            System.out.println("Uploading file to s3 bucket");
            File filename = convertFromMultipart(multipartfile);
            s3Client.putObject(new PutObjectRequest(bucketName, transactionDetail.getTransactionDetailsId().toString()+"/"+filename.getName(),filename));
            return transactionDetail.getTransactionDetailsId().toString()+filename.getName();
        }catch(AmazonServiceException ase){
            System.out.println("bucket name: " + bucketName);
            System.out.println("Request made to s3 bucket failed");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method converts a multipart file to File format
     * @param file : Transaction Attachment
     */
    public File convertFromMultipart(MultipartFile file) throws Exception
    {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
