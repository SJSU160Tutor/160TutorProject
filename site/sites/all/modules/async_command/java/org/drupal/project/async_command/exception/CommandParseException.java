package org.drupal.project.async_command.exception;

public class CommandParseException extends Exception {
    //@Override
    public CommandParseException(String msg) {
        super(msg);
    }

    //@Override
    public CommandParseException(String msg, Throwable e) {
        super(msg, e);
    }
}
