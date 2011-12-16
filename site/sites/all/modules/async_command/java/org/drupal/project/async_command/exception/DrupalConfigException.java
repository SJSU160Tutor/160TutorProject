package org.drupal.project.async_command.exception;

public class DrupalConfigException extends RuntimeException {

    public DrupalConfigException(String message) {
        super(message);
    }

    public DrupalConfigException(Throwable cause) {
        super(cause);
    }
}
