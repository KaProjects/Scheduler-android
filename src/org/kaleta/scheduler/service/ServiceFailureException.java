package org.kaleta.scheduler.service;

/**
 * Author: Stanislav Kaleta
 * Date: 27.7.2015
 */
public class ServiceFailureException extends RuntimeException {

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceFailureException(String message) {
        super(message);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException() {
    }
}
