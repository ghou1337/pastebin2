package pl.pastebin.cloud.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pastebin.model.Data;
import pl.pastebin.model.DataCloudResponse;
import pl.pastebin.model.MetadataDAO;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class GoogleCloudStorageService {
    private final Storage storage;
    @Value("${bucket.name}")
    private String bucketName;

    private BlobInfo createBlobInfo(Data data) {
        return BlobInfo
                .newBuilder(bucketName, data.getHash())
                .setContentType("text/plain")
                .build();
    }

    public String uploadFile(Data data, MetadataDAO metadataDAO) {
       BlobInfo blobInfo = createBlobInfo(data)
               .toBuilder().setMetadata(Map.of(
                       "name", String.valueOf(data.getName()),
                       "created_at", String.valueOf(metadataDAO.getCreatedAt()),
                       "expression_date", String.valueOf(metadataDAO.getExpressionDate())
               ))
               .build();
        try {
            storage.create(blobInfo, data.getText().getBytes(UTF_8));
        } catch (StorageException e) {
            throw new StorageException (e.getCode(),  "Error creating blob", e);
        }

        // Sending an event
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, data.getHash());
    }

    public DataCloudResponse getFile(String hash) {
        Blob file = storage.get(bucketName, hash);
        if(file == null) {
            throw new StorageException(404, "File not found");
        }
        String content = new String(file.getContent(), UTF_8);
        if (file.getContent() == null || file.getContent().length == 0) {
            throw new StorageException(400, "File is empty");
        }
        return new DataCloudResponse(file.getName(), content, hash);
    }
}
