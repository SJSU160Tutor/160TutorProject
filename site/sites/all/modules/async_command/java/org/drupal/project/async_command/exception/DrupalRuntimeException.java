package org.drupal.project.async_command.exception;

/**
 * Handles any Drupal related exceptions.
 */
public class DrupalRuntimeException extends RuntimeException {
    public DrupalRuntimeException(Throwable t) {
        super(t);
    }
    public DrupalRuntimeException(String msg) {
        super(msg);
    }
}
