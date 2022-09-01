package com.example.intermediate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
@Service
@Getter
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFiles(MultipartFile multipartFile , String dirName)throws IOException{
        File uploadFile =convert(multipartFile)
                .orElseThrow(()->new IllegalArgumentException("ERROR :  MultipartFile -> File로 전환이 실패했습니다."));

        return upload(uploadFile,dirName);
    }

    public String upload(File uploadFile, String dirName){
        String fileName = dirName+ "/"+ "/" + uploadFile.getName();

        String uploadImageUrl = putS3(uploadFile,fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    public String putS3(File uploadFile, String fileName){
        amazonS3Client.putObject(new PutObjectRequest(bucket,fileName,uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket,fileName).toString();
    }

    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            System.out.println("파일이 삭제되었습니다");
            return;
        }
        System.out.println("파일 삭제에 실패했습니다.");
    }


    //로컬에 파일 업로드하기
    private Optional<File> convert(MultipartFile file) throws IOException{
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File convertFile = new File(file.getOriginalFilename());

        if(convertFile.createNewFile()){
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

}
