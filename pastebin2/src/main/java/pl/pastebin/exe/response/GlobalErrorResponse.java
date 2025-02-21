package pl.pastebin.exe.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GlobalErrorResponse {
    private String message;
    private long timestamp;
}
