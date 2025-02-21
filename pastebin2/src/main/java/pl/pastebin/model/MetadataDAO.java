package pl.pastebin.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class MetadataDAO {
    private Timestamp createdAt;
    private Timestamp expressionDate;
}
