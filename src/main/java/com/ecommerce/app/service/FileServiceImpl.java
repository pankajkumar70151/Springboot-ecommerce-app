package com.ecommerce.app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl  implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {

        // Get file original file name

        String fileName = image.getOriginalFilename();

        // Generate a unique file name
        String uniqueFileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
        String filePath = path + File.separator + uniqueFileName;

        // check if path exist and create
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdir();

        // upload to server
        Files.copy(image.getInputStream(), Paths.get(filePath));

        //return the updated file name
        return uniqueFileName;
    }
}
