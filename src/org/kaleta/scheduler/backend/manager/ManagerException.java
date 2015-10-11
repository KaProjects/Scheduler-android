package org.kaleta.scheduler.backend.manager;

/**
 * Author: Stanislav Kaleta
 * Date: 23.7.2015
 */
public class ManagerException extends Exception {

    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerException(String message) {
        super(message);
    }

    public ManagerException(Throwable cause) {
        super(cause);
    }

    public ManagerException() {
    }
}
