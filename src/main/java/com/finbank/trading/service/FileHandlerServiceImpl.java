package com.finbank.trading.service;

import com.finbank.trading.config.ApplicationProperties;
import com.finbank.trading.model.ResponseTuple;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
public class FileHandlerServiceImpl implements FileHandlerService{

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private TransactionsProducer transactionsProducer;

    @Override
    public String storeFile(MultipartFile file) {

        String uploadDirectory = applicationProperties.getUploadDirectory();

        Path fileStorageLocation = Paths.get(uploadDirectory).toAbsolutePath().normalize();

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            transactionsProducer.loadTransactions(String.format("%s%s", uploadDirectory, fileName));

            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }

    }

    @Override
    public File writeCsvFileToDownloads(List<ResponseTuple> result) throws Exception {

        String fileName = String.format("%s%s", applicationProperties.getDownloadDirectory(), applicationProperties.getDownloadFleName());

        Writer writer = new FileWriter(fileName);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        beanToCsv.write(result);
        writer.close();

        return new File(fileName);
    }


}
