package com.fileupload.controller;

import com.fileupload.model.User;
import com.fileupload.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) {
        return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String filename) {
        byte[] data = storageService.downloadFile(filename);
        ByteArrayResource bar = new ByteArrayResource(data);
        return ResponseEntity.ok().contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\""+filename+"\"")
                .body(bar);
    }

    @GetMapping("/profilepicName/{username}")
    public ResponseEntity<String> getProfilePicName(@PathVariable String username) {
        return new ResponseEntity(storageService.getProfilePicName(username), HttpStatus.OK);
    }



    @PostMapping("update/profilepic/{username}")
    public ResponseEntity<?> updateProfilepic(@PathVariable String username, @RequestParam("imageData") MultipartFile imageData) {

        Optional<User> opUser =  storageService.findUserByUsername(username);
        if (opUser.isPresent()) {

                User user = opUser.get();
                String oldFilename = user.getAvatarLink();
                String newFilename = storageService.uploadFile(imageData);
            try {
                if (oldFilename.trim().length() >10){}
//                    storageService.deleteFile(oldFilename);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
                user.setAvatarLink(newFilename);
                User updatedUser = storageService.updateUser(user);
                return new ResponseEntity(updatedUser, HttpStatus.OK);
        }
        else return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);

    }
}
