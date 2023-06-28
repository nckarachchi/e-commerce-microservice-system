package com.ecommerce.fileservice.service;

import com.ecommerce.fileservice.exception.FileExistSameNameException;
import com.ecommerce.fileservice.exception.FileUploadException;
import com.ecommerce.fileservice.exception.NotAnImageFileException;
import com.ecommerce.fileservice.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

@Service
@Slf4j
public class FileService {

    public String saveImage(MultipartFile image) {
        if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(image.getContentType())) {
            throw new NotAnImageFileException(image.getOriginalFilename() + FileConstant.NOT_AN_IMAGE_FILE);
        }
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId+".jpg";
        try {
            Path imageFolder = Paths.get(FileConstant.IMAGE_FOLDER).toAbsolutePath().normalize();
            Path imageFolderAndFile = Paths.get(FileConstant.IMAGE_FOLDER+ FileConstant.FORWARD_SLASH+ randomId).toAbsolutePath().normalize();
            if(!Files.exists(imageFolderAndFile)) {
                Files.createDirectories(imageFolder);
                log.info(FileConstant.DIRECTORY_CREATED + imageFolder);
            }else{
                throw new FileExistSameNameException(FileConstant.FILE_EXIST_ERROR);
            }
            Files.copy(image.getInputStream(), imageFolder.resolve(fileName), REPLACE_EXISTING);
            log.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM + image.getOriginalFilename());
        }catch (IOException e){
            throw new FileUploadException(FileConstant.FILE_UPLOAD_ERROR);
        }

        return FileConstant.FILE_SERVER + fileName;
    }

    public String removeImage(String imagePath) {
        try {
            Path imageFolder = Paths.get(FileConstant.IMAGE_FOLDER).toAbsolutePath().normalize();
            log.info(FileConstant.DIRECTORY_CREATED + imageFolder);

            Files.delete(imageFolder.resolve(imagePath));
            log.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM + imagePath);
        }catch (Exception e){
            throw new FileUploadException(FileConstant.FILE_REMOVE_ERROR);
        }

        return FileConstant.FILE_SERVER + imagePath;
    }

}
