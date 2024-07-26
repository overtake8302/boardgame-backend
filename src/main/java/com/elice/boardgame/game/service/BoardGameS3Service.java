package com.elice.boardgame.game.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BoardGameS3Service {

    private final S3Client amazonS3Client1;

    @Value("${bucket1.name}")
    private String bucket1Name;

    public BoardGameS3Service(S3Client amazonS3Client1) {
        this.amazonS3Client1 = amazonS3Client1;
    }

    public List<String> uploadFileToBucket1(MultipartFile file) throws IOException {
        return uploadFile(file, bucket1Name);
    }

    private List<String> uploadFile(MultipartFile file, String bucketName) throws IOException {

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PutObjectResponse response = amazonS3Client1.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        String url = amazonS3Client1.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();

        List<String> info = new ArrayList<>();
        info.add(fileName);
        info.add(url);

        return info;
    }

    public void deleteFileFromBucket1(String fileName) {
        deleteFile(fileName, bucket1Name);
    }

    private void deleteFile(String fileName, String bucketName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        amazonS3Client1.deleteObject(deleteObjectRequest);
    }
}
