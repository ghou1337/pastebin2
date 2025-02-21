package pl.pastebin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataCloudResponse {
    private String name;
    private String text;
    private String hash;
}
