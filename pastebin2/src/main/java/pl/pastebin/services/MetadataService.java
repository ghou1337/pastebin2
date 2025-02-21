package pl.pastebin.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pastebin.exe.MetadataSavingException;
import pl.pastebin.exe.NoSuchDateException;
import pl.pastebin.model.Data;
import pl.pastebin.model.Metadata;
import pl.pastebin.model.MetadataDAO;
import pl.pastebin.repos.MetadataRepos;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataService {
    private final MetadataRepos metadataRepos;

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public String saveMetadataFromFile(Data data, MetadataDAO metadataDAO) {
        final String name = data.getName();
        final String hash = data.getHash();
        final Timestamp createdAt = Optional.ofNullable(metadataDAO.getCreatedAt())
                .orElseThrow(() -> new IllegalArgumentException("CreatedAt cannot be null"));
        final Timestamp expirationDate = Optional.ofNullable(metadataDAO.getExpressionDate())
                .orElseThrow(() -> new IllegalArgumentException("ExpirationDate cannot be null"));

        Metadata metadata = new Metadata(null, name, hash, createdAt, expirationDate);
        try {
            metadataRepos.save(metadata);
        } catch (RuntimeException e) {
            throw new MetadataSavingException("Integrity violation while saving metadata", e);
        }

        return metadata.getHash();
    }

    public MetadataDAO setMetadataDate(Data data) {
        final Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());
        final Timestamp ExpressionDate = Timestamp.valueOf(LocalDateTime.now().plusDays(data.getExpressionDays()));
        return new MetadataDAO(createdAt, ExpressionDate);
    }

    public Timestamp getExpressionDate(String hash) {
        return metadataRepos.findByHash(hash).getExpressionDate();
    }

    public Timestamp getCreatedAt(String hash) {
        try {
            return metadataRepos.findByHash(hash).getCreatedAt();
        } catch (RuntimeException e) {
            throw new NoSuchDateException("No such date error");
        }
    }
}
