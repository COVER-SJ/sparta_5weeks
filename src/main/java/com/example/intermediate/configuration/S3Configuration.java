package com.example.intermediate.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class S3Configuration {

    @Value("${cloud.aws.credentials.access-Key}")
    private String accessKey;

    @Value("ZYZJ7q4L3yuWsNfU+GloVUD+T8Mkqu2kfQ1hz2/e")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;


    @Bean
    public AmazonS3Client amazonS3Client(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }


}
