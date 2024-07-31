package com.elice.boardgame.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonS3Config {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${bucket1.access-key}")
    private String bucket1AccessKey;

    @Value("${bucket1.secret-key}")
    private String bucket1SecretKey;

    @Bean
    @Primary
    public S3Client amazonS3Client1() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(bucket1AccessKey, bucket1SecretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
