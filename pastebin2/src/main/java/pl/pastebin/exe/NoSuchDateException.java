package pl.pastebin.exe;

public class NoSuchDateException extends RuntimeException{
    public NoSuchDateException(String message) {
        super(message);
    }
}
