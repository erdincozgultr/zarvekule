package com.zarvekule.file.service;

import com.zarvekule.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final String UPLOAD_ROOT = "uploads";
    private static final int TARGET_WIDTH = 800;

    @Override
    public String uploadImage(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new ApiException("Dosya boş olamaz.", HttpStatus.BAD_REQUEST);
        }

        Path uploadPath = Paths.get(UPLOAD_ROOT, subDirectory);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new ApiException("Dosya yükleme klasörü oluşturulamadı.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(fileName);

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new ApiException("Geçersiz resim dosyası.", HttpStatus.BAD_REQUEST);
            }

            BufferedImage resizedImage = resizeImage(originalImage, TARGET_WIDTH);

            String formatName = extension.replace(".", "");
            ImageIO.write(resizedImage, formatName, filePath.toFile());

        } catch (IOException e) {
            throw new ApiException("Resim işlenirken hata oluştu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return "/uploads/" + subDirectory + "/" + fileName;
    }

    @Override
    public void deleteImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isEmpty()) return;

        String relativePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
        Path filePath = Paths.get(relativePath);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Dosya silinemedi: " + filePath);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= targetWidth) {
            return originalImage;
        }

        int targetHeight = (int) ((double) targetWidth / originalWidth * originalHeight);

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}