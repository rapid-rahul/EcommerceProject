package org.gmi.ecommerceproject.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // 1. Get the original filename and create a unique filename.
        String originalFileName = file.getOriginalFilename();
        String extension = null;
        if (originalFileName != null) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 2. Create the target directory if it doesn't exist.
        Path uploadPath = Paths.get(path);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Construct the full file path.
        Path filePath = uploadPath.resolve(uniqueFileName);

        // 4. Copy the file to the target location.
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 5. Return the unique filename.
        return uniqueFileName;
    }
}
