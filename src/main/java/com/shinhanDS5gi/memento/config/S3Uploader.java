package com.shinhanDS5gi.memento.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class S3Uploader {

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.upload-dir}")
    private String uploadDir;

    public String upload(MultipartFile file) throws IOException {
        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + (ext != null ? "." + ext : "");
        String key = uploadDir + "/" + fileName;

        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                )
                .build();

        // txt 인코딩
        String contentType = file.getContentType();

        if (contentType != null && contentType.startsWith("text/")) {
            contentType += "; charset=utf-8";
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectResponse response = s3.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("S3 업로드 실패: " + response);
        }

        // 바로 접근 가능한 URL 반환
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + URLEncoder.encode(key, StandardCharsets.UTF_8);
    }

    /* S3에서 파일 삭제 */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        try {
            // URL에서 S3 객체 키(파일 경로)를 추출
            String key = fileUrl.substring(fileUrl.indexOf(uploadDir));

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            System.err.println("S3 파일 삭제 실패: " + fileUrl + ", error: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return null;
    }
}
