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
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.neu.pojo.TransactionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;

public class DeleteAttachmentS3BucketController {

    @Autowired
    private Environment environment;
    ObjectMetadata objectMetadata = new ObjectMetadata();


    public String deleteFile(TransactionDetails transactionDetail, String keyName){

      //  String bucketName = System.getProperty("bucket.name");
      //  System.out.println("Delete file from bucket, bucket name is :" + System.getProperty("bucket.name"));

        /*Assigns Temporary credentials to IAM role
         * InstanceProfileCredentialsProvider : false does not refresh the credentials
         */
        System.out.println("IN delete method");
        String bucketN = environment.getProperty("bucket.name");
        System.out.println("Bucket name" + bucketN);

        InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider
                (true);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(Regions.US_EAST_1).build();


//        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
//
//        AmazonS3 s3Client = AmazonS3ClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .build();

        System.out.println("IN delete after build");
        /*InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider
                (true);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(Regions.US_EAST_1).build();
        System.out.println("InstanceProfileCreated");*/
        String bucketName = null;



        List<Bucket> buckets = s3Client.listBuckets();
        for(Bucket bucket : buckets) {
            System.out.println(bucket.getName());

            if(bucket.getName().equals(bucketN))
            {
                bucketName=bucket.getName();
                System.out.println("BUCKET FOUND");
                break;
            }

        }
        try{

            System.out.println("Deleting file from S3 bucket!!");
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, transactionDetail.getTransactionDetailsId().toString()+"/"+keyName));
            return "deleted";
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
            System.out.println("----------Stack Trace------\n" + e.getStackTrace());
            return null;
        }
    }
}
