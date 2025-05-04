package org.greencodeinitiative.tools.exporter.infra;

public class ProcessException extends RuntimeException {
    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
