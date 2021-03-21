package org.crumbs.core.exception;

public class CrumbsInitException extends RuntimeException {
    public CrumbsInitException(String message) {
        super(message);
    }
    public CrumbsInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
