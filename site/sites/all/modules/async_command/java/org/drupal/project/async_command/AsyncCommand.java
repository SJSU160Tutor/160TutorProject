package org.drupal.project.async_command;

import java.util.logging.Logger;

/**
 * Individual command to be executed. Each command is also registered with a DrupalApp.
 * A command doesn't necessarily know a DrupalConnection. If needed, it can get from DrupalApp.
 * The Record inner class needs to know a DrupalConnection in order to do database operations.
 */
abstract public class AsyncCommand implements Runnable {

    protected static Logger logger = DrupalUtils.getPackageLogger();

    public static enum Status {
        SUCCESS("OKOK"),
        FAILURE("FAIL"),
        RUNNING("RUNN"),
        FORCED_STOP("STOP"),
        UNRECOGNIZED("NREC"),
        PENDING("PEND");


        private final String statusToken;

        Status(String token) {
            assert token.length() == 4;
            this.statusToken = token;
        }

        @Override
        public String toString() {
            return statusToken;
        }

        public static Status parse(String token) {
            for (Status status : Status.class.getEnumConstants()) {
                if (status.statusToken.equals(token)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Cannot parse status token: " + token);
        }
    }

    /**
     * The drupal application this command is associated with.
     */
    protected final GenericDrupalApp drupalApp;

    /**
     * The database record this command is associated with.
     */
    protected final CommandRecord record;

    /**
     * Constructor should prepare the command to run "run()". Set member fields by using data in "record"; don't use record directly in execution.
     *
     * @param record
     * @param drupalApp
     */
    public AsyncCommand(CommandRecord record, GenericDrupalApp drupalApp) {
        assert record != null && drupalApp != null;
        this.record = record;
        this.drupalApp = drupalApp;
    }

    /**
     * Specifies the name this command is known as. By default is the class name. You can override default value too.
     * Deprecated because we don't want to use the default identifier. Instead, when register the command with an app, you can specify the identifier for this command on that specific
     *
     * @return The identifier of the command.
     */
    public String getIdentifier() {
        return DrupalUtils.getIdentifier(this.getClass());
    }

    /**
     * Run this command. All parameters should be set before executing this command, preferably in the constructor.
     * After execution, the field 'record' should have the final results.
     * This method is not responsible to save status results back to the database. It's only responsible to run the command and set the command record.
     */
    @Override
    abstract public void run();


    /**
     * Override this if you want to evaluate the command from CLI or other ad-hoc approach.
     *
     * @param params
     * @return The object of this AsyncCommand class. The caller is responsible to run/update the object.
     */
    public AsyncCommand evaluate(String... params) {
        throw new UnsupportedOperationException("This command does not support evaluate() method.");
    }


}
