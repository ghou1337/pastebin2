package pl.pastebin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pastebin.exe.InvalidDateException;
import pl.pastebin.exe.InvalidTextException;
import pl.pastebin.exe.MetadataSavingException;
import pl.pastebin.exe.NoSuchDateException;
import pl.pastebin.exe.response.GlobalErrorResponse;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class SharingControllerExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(NoSuchElementException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Element wasn't found",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(NoSuchDateException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Date wasn't found",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(MetadataSavingException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        log.error("Failed to save metadata: {}", e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(InvalidTextException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Your text should be above 3 character",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(InvalidDateException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Your expression date should be above 0 and less than 60 days",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
