package io.github.slfotg.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
class FileSystemStorageService implements StorageService {

    private File storageLocation;

    public FileSystemStorageService(@Value("${upload.storageLocation}") File storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public void store(MultipartFile file) throws IOException {
        File outputFile = createUniqueFile();
        log.info("File uploaded: {}", outputFile.getName());
        try (InputStream input = file.getInputStream();
                OutputStream output = new FileOutputStream(outputFile)) {
            IOUtils.copy(input, output);
        }
        Files.createFile(Paths.get(storageLocation.getPath(), outputFile.getName() + ".complete"));
    }

    private File createUniqueFile() {
        return new File(storageLocation, UUID.randomUUID().toString());
    }

}
