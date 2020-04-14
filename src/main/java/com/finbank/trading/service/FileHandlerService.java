package com.finbank.trading.service;

import com.finbank.trading.model.ResponseTuple;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileHandlerService {

    String storeFile(MultipartFile file);

    File writeCsvFileToDownloads(List<ResponseTuple> result) throws Exception;
}
