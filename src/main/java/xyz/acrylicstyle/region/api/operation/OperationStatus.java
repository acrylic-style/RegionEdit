package xyz.acrylicstyle.region.api.operation;

public enum OperationStatus {
    /**
     * Currently running.
     */
    RUNNING,
    /**
     * Cancelled while running.
     */
    CANCELLED,
    /**
     * Operation was finished.
     */
    FINISHED,
}
