package pl.pastebin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pastebin.client.HashClient;
import pl.pastebin.exe.*;
import pl.pastebin.model.*;
import pl.pastebin.services.MetadataService;
import pl.pastebin.cloud.service.GoogleCloudStorageService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SharingController {
    private final GoogleCloudStorageService googleCloudStorageService;
    private final MetadataService metadataService;
    private final HashClient hashClient;


    @PostMapping("/post")
    public ResponseEntity<Data> postFile(@RequestBody DataDAO dataDAO) {
        final MetadataDAO metadataDAO;

        if (dataDAO.getExpressionDays() > 60 || dataDAO.getExpressionDays() <= 0) {
            throw new InvalidDateException();
        }
        if (dataDAO.getText().isBlank() || dataDAO.getText().length() < 3) {
            throw new InvalidTextException();
        }
        final Data data = new Data(dataDAO, (hashClient.getHash().block()));

        metadataDAO = metadataService.setMetadataDate(data);
        metadataService.saveMetadataFromFile(data, metadataDAO);
        googleCloudStorageService.uploadFile(data, metadataDAO);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{hash}")
    private DataCloudResponse getFileTextByHash(@PathVariable String hash) {
        return googleCloudStorageService.getFile(hash);

    }

    @GetMapping("/metadata/{hash}")
    private MetadataDAO getFileMetadataByHash(@PathVariable String hash) {
        return new MetadataDAO(metadataService.getCreatedAt(hash), metadataService.getExpressionDate(hash));
    }
}