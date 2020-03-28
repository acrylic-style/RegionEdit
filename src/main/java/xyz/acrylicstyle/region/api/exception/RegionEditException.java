package xyz.acrylicstyle.region.api.exception;

public class RegionEditException extends RuntimeException {
    public RegionEditException(String message) {
        super(message);
    }

    public RegionEditException(String message, Throwable cause) {
        super(message, cause);
    }
}
