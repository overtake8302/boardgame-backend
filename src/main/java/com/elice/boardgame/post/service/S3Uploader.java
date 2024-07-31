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

@Service  // Spring의 서비스 컴포넌트로 등록
public class S3Uploader {
    private final S3Client s3Client;  // S3 클라이언트 객체
    private final String bucketName;  // S3 버킷 이름

    // 생성자: AWS 자격 증명과 버킷 이름을 받아 S3 클라이언트를 초기화
    public S3Uploader(@Value("${aws.accessKey}") String accessKey, @Value("${aws.secretKey}") String secretKey, @Value("${aws.bucketName}") String bucketName) {
        this.s3Client = S3Client.builder()
            .region(Region.AP_NORTHEAST_2)  // S3 클라이언트의 리전 설정
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))  // AWS 자격 증명 설정
            .build();
        this.bucketName = bucketName;  // 버킷 이름 설정
    }

    // S3에 파일을 업로드하는 메서드
    public String upload(String filePath, String fileName) {
        // PutObjectRequest 객체를 생성하여 S3 버킷과 파일 키를 설정
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)  // 업로드할 버킷 설정
            .key(fileName)  // 업로드할 파일의 키 설정
            .build();

        // 파일을 S3에 업로드
        PutObjectResponse response = s3Client.putObject(putObjectRequest, Paths.get(filePath));
        // 업로드된 파일의 URL 반환
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();
    }

    // MultipartFile을 S3에 업로드하는 메서드
    public String uploadFile(MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {  // 파일이 비어 있지 않은 경우
            String fileName = file.getOriginalFilename();  // 파일 이름 가져오기
            Path tempFile = Files.createTempFile("temp", fileName);  // 임시 파일 생성
            file.transferTo(tempFile.toFile());  // MultipartFile을 임시 파일로 전송
            String imageUrl = upload(tempFile.toString(), fileName);  // 임시 파일을 S3에 업로드
            Files.delete(tempFile);  // 임시 파일 삭제
            return imageUrl;  // 업로드된 파일의 URL 반환
        }
        return null;  // 파일이 비어 있으면 null 반환
    }
}