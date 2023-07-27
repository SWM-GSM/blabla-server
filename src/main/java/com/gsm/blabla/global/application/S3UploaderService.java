package com.gsm.blabla.global.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Component
@Transactional
@RequiredArgsConstructor
public class S3UploaderService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 업로드
    public String uploadImage(MultipartFile multipartFile, String dirName) {
        // 메타테이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        // 실제 S3 bucket 디렉토리명 설정
        // 파일명 중복을 방지하기 위해 UUID 추가
        String fileName = dirName + "/" + UUID.randomUUID() + "." + multipartFile.getOriginalFilename();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalStateException("S3 파일 업로드에 실패했습니다.");
        }

        String profileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        log.info("S3 업로드 완료: {}", profileUrl);

        return profileUrl;
    }

    public List<String> uploadWavFiles(Long crewId, Long reportId, List<String> userIdList, List<MultipartFile> wavFiles, String dirName) {
        List<String> fileUrls = new ArrayList<>();
        IntStream.range(0, wavFiles.size()).forEach(index -> {
            MultipartFile wavFile = wavFiles.get(index);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(wavFile.getContentType());
            objectMetadata.setContentLength(wavFile.getSize());

            // 파일명 중복을 방지하기 위해 UUID 추가
            String fileName = String.format("%s/%s/%s/%s/%s.wav", dirName, userIdList.get(index), String.valueOf(crewId), String.valueOf(reportId), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm")));

            try (InputStream inputStream = wavFile.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new IllegalStateException("S3 음성 파일 업로드에 실패했습니다.");
            }

            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
            fileUrls.add(fileUrl);
        });
        return fileUrls;
    }

    // 이미지 삭제
    public void deleteImage(String originalFileName) {
        amazonS3Client.deleteObject(bucket, originalFileName);
    }
}
