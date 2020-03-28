package xyz.acrylicstyle.region.api.exception;

public class RegionSelectorException extends RuntimeException {
    public RegionSelectorException(String message) {
        super(message);
    }

    public RegionSelectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
