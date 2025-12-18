package com.zarvekule.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file, String subDirectory);

    void deleteImage(String imageUrl);
}