package org.drupal.project.async_command.exception;

import org.drupal.project.async_command.exception.DrupalRuntimeException;

/**
 * Handles database related exceptions.
 */
public class DrupalDatabaseException extends DrupalRuntimeException {
    public DrupalDatabaseException(Throwable t) {
        super(t);
    }

    public DrupalDatabaseException(String msg) {
        super(msg);
    }
}
