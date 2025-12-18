package com.zarvekule.file.controller;

import com.zarvekule.file.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final ImageService imageService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {

        String subDirectory = switch (type) {
            case "avatar" -> "avatars";
            case "homebrew" -> "homebrew-images";
            case "market" -> "market-images";
            case "blog" -> "blog-images";
            case "wiki" -> "wiki-images";
            default -> "general";
        };

        String fileUrl = imageService.uploadImage(file, subDirectory);

        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}