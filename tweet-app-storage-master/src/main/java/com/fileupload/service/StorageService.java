package com.fileupload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.fileupload.model.User;
import com.fileupload.repository.UserRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class StorageService {

    @Autowired
    UserRepository userRepository;

    @Value("${application.bucket.name}")
    String bucketName;

    @Autowired
    private AmazonS3 s3client;

    public String uploadFile(MultipartFile file) {
        String filename = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        File convertedFile = convertMultiPartFileToFile(file);
        PutObjectResult a = s3client.putObject(new PutObjectRequest(bucketName, filename, convertedFile));
        System.out.println(a.getContentMd5() + ",,,"+ a.getVersionId());
        convertedFile.delete();
        return filename;
    }

    public byte[] downloadFile(String filename) {
        byte[] content = null;
        S3Object s3Object = s3client.getObject(bucketName, filename);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile))
        {
                fos.write(file.getBytes());
        }

        catch (Exception e) {
            System.out.println("error");
        }
        return convertedFile;
    }

    public String deleteFile (String filename) {
        s3client.deleteObject(bucketName, filename);
        return "file deleted "+filename;
    }

    public Optional<User> findUserByUsername(String username){
       return userRepository.findUserByUsername(username);
    }

    public User updateUser(User user) {
        User updatedUser = userRepository.save(user);
        updatedUser.setPassword("");
        updatedUser.setSecurityAnswer("");
        return updatedUser;
    }

    public String getProfilePicName(String username) {
      Optional<User> opUser = userRepository.findUserByUsername(username);
      if(opUser.isPresent()){
          return opUser.get().getAvatarLink();
      }
      return null;
    }
}
