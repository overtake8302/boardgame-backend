package com.elice.boardgame.post.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class S3Uploader {
    private final S3Client s3Client;
    private final String bucketName;

    public S3Uploader(@Value("${aws.accessKey}")String accessKey, @Value("${aws.secretKey}")String secretKey, @Value("${aws.bucketName}")String bucketName) {
        this.s3Client = S3Client.builder()  //  S3 클라이언트를 빌더 패턴으로 생성
            .region(Region.AP_NORTHEAST_2)  //  S3 클라이언트의 리전설정
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))  //  AWS 자격증명,엑세스 키와 시크릿 키를 제공
            .build();
        this.bucketName = bucketName;  //  버킷 이름 설정
    }

    //  S3에 파일을 업로드하는 메서드
    public String upload(String filePath, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)  //  업로드할 버킷 설정
            .key(fileName)  //  업로드할 파일의 키 설정
            .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, Paths.get(filePath));  //  파일을 S3에 업로드
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();  //  업로드된 파일의 URL 반환
    }

    public String uploadFile(MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path tempFile = Files.createTempFile("temp", fileName);
            file.transferTo(tempFile.toFile());
            String imageUrl = upload(tempFile.toString(), fileName);
            Files.delete(tempFile);
            return imageUrl;
        }
        return null;
    }
}
