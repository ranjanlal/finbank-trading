package com.finbank.trading.controller;

import com.finbank.trading.model.ApiResponse;
import com.finbank.trading.model.ResponseTuple;
import com.finbank.trading.service.FileHandlerService;
import com.finbank.trading.service.TransactionsReportService;
import com.finbank.trading.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/api")
public class TradeTransactionsController {

    @Autowired
    Messages messages;

    @Autowired
    private TransactionsReportService transactionsReportService;

    @Autowired
    private FileHandlerService fileHandlerService;

    /**
     * Welcome API
     *
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<ApiResponse> welcome() {
        return ResponseEntity.ok()
                .body(new ApiResponse(
                        HttpStatus.OK,
                        messages.get("welcome.message"),
                        Optional.empty()));
    }

    /**
     * Upload futures transactions file
     *
     * @param file
     * @return
     */
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse> transactions(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileHandlerService.storeFile(file);

            return ResponseEntity.ok()
                    .body(new ApiResponse(
                            HttpStatus.OK,
                            fileName + " uploaded successfully",
                            Optional.empty()));

        } catch (Exception ex) {
            log.error("Unable to upload file : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to upload file", ex);
        }
    }


    /**
     * Generate summary report for all futures transactions by all clients
     *
     * @return
     */
    @GetMapping("/report/all")
    public ResponseEntity<List<ResponseTuple>> report() {
        try {
            List<ResponseTuple> result = transactionsReportService.generateReport();

            return ResponseEntity.ok()
                    .body(result);
        } catch (Exception ex) {
            log.error("Unable to generate report : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate report", ex);
        }
    }

    /**
     * Generate report (in JSON format) for all futures transactions done by a given client on a given date.
     * Optional : transaction date. If not provided returns all transactions done by a given client
     *
     * @param clientNumber
     * @param transactionDate
     * @return
     */
    @GetMapping(value = "/report/{clientNumber}", produces = "application/json")
    public ResponseEntity<List<ResponseTuple>> report(@PathVariable(value = "clientNumber") String clientNumber,
                                                      @RequestParam(value = "transactionDate") Optional<String> transactionDate) {
        try {
            List<ResponseTuple> result = transactionDate.isPresent()
                    ? transactionsReportService.generateReport(clientNumber, transactionDate.get())
                    : transactionsReportService.generateReport(clientNumber);

            return ResponseEntity.ok()
                    .body(result);
        } catch (Exception ex) {
            log.error("Unable to generate report : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate report", ex);
        }
    }

    /**
     * Generate report (in CSV format) for all futures transactions done by a given client on a given date.
     * Optional : transaction date. If not provided returns all transactions done by a given client
     *
     * @param clientNumber
     * @return
     */
    @GetMapping(value = "/report/csv/{clientNumber}", produces = "text/csv")
    public ResponseEntity reportCsv(@PathVariable(value = "clientNumber") String clientNumber,
                                    @RequestParam(value = "transactionDate") Optional<String> transactionDate) {

        try {

            File csvFile = transactionDate.isPresent()
                    ? transactionsReportService.generateReportCSV(clientNumber, transactionDate.get())
                    : transactionsReportService.generateReportCSV(clientNumber);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            String.format("attachment; filename=%s", csvFile.getName()))
                    .contentLength(csvFile.length())
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new FileSystemResource(csvFile));

        } catch (Exception ex) {
            log.error("Unable to generate report : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate report", ex);
        }
    }

}
