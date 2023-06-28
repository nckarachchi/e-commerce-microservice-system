package com.ecommerce.fileservice.controller;

import com.ecommerce.fileservice.service.FileService;
import com.ecommerce.fileservice.constant.FileConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    @PostMapping("/saveImage")
    public ResponseEntity<String> saveImage(@RequestParam("file") MultipartFile image) {
        return ResponseEntity.ok(fileService.saveImage(image));
    }

    @GetMapping(path = "/image/{imageName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstant.IMAGE_FOLDER  + FileConstant.FORWARD_SLASH + imageName));
    }

    @DeleteMapping("/removeImage")
    public ResponseEntity<String> removeImage(@RequestParam String imagePath) {
        return ResponseEntity.ok(fileService.removeImage(imagePath));
    }
}
