package org.drupal.project.async_command;

import org.drupal.project.async_command.exception.CommandParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This is the new DrupalApp class. A launcher has a field to launch DrupalApp, rather than DrupalApp has a field of launcher.
 */
public class GenericDrupalApp implements Runnable {

    /**
     * Various running mode of the DrupalApp
     */
    public static enum RunningMode {
        /**
         * Only execute the next available command in the queue, and then exit.
         */
        FIRST,

        /**
         * Retrieve a list of commands, execute them in serial, and then exit.
         */
        SERIAL,

        /**
         * Retrieves a list of commands, execute them in parallel, and then exit.
         */
        PARALLEL,

        /**
         * Continuously retrieve new commands from the queue, execute them in parallel (serial just means thread=1), and exit only when told to.
         */
        NONSTOP;
    }

    private RunningMode runningMode;

    protected DrupalConnection drupalConnection;

    protected static Logger logger = DrupalUtils.getPackageLogger();

    /**
     * Class name: the class object.
     */
    protected Map<String, Class> acceptableCommandClass = new HashMap<String, Class>();

    /**
     * Register acceptable AsyncCommand classes in constructor. By default, this registers PingMe command.
     * Any DrupalApp should have at least one drupal database connection. Otherwise it's not "DrupalApp" anymore.
     *
     * @param drupalConnection Connection to a Drupal database that has the {async_command} table.
     */
    public GenericDrupalApp(DrupalConnection drupalConnection) {
        assert drupalConnection != null;
        runningMode = RunningMode.SERIAL;
        setDrupalConnection(drupalConnection);
        // register command classes
        registerCommandClass(PingMe.class);
    }


    /////////////////////////////////////////////////


    public void setRunningMode(RunningMode runningMode) {
        this.runningMode = runningMode;
    }

    /**
     * Only derived class can change drupal connection.
     *
     * @param drupalConnection
     */
    protected void setDrupalConnection(DrupalConnection drupalConnection) {
        assert drupalConnection != null;
        this.drupalConnection = drupalConnection;
    }

    public DrupalConnection getDrupalConnection() {
        return this.drupalConnection;
    }

    /**
     * Specifies the name this DrupalApp is known as. By default is the class name. You can override default value too.
     * @return The identifier of the app.
     */
    public String getIdentifier() {
        return DrupalUtils.getIdentifier(this.getClass());
    }


    /**
     * Create an object of AsyncCommand based on the CommandRecord.
     * This function can't be moved into CommandRecord because CommandRecord is not award of different AsyncCommand classes.
     *
     * @param record
     * @return
     * @throws CommandParseException
     */
    AsyncCommand parseCommand(CommandRecord record) throws CommandParseException {
        if (acceptableCommandClass.containsKey(record.getCommand())) {
            Class commandClass = acceptableCommandClass.get(record.getCommand());
            try {
                Constructor<AsyncCommand> constructor = commandClass.getConstructor(CommandRecord.class, GenericDrupalApp.class);
                return constructor.newInstance(record, this);
            } catch (NoSuchMethodException e) {
                throw new CommandParseException("Cannot construct command object.", e);
            } catch (InvocationTargetException e) {
                throw new CommandParseException("Cannot construct command object.", e);
            } catch (InstantiationException e) {
                throw new CommandParseException("Cannot construct command object.", e);
            } catch (IllegalAccessException e) {
                throw new CommandParseException("Cannot construct command object.", e);
            }
        } else {
            throw new CommandParseException("Invalid command or not registered with the DrupalApp. Command: " + record.getCommand());
        }
    }

    /**
     * Register a command with the Drupal application.
     *
     * @param commandClass
     */
    public void registerCommandClass(Class<? extends AsyncCommand> commandClass) {
        // the command class has to be a subclass of AsyncCommand
        // assert AsyncCommand.class.isAssignableFrom(commandClass);
        String id = DrupalUtils.getIdentifier(commandClass);
        acceptableCommandClass.put(id, commandClass);
    }


    /*public void registerCommandClass(String identifier, Class commandClass) {
        // the command class has to be a subclass of AsyncCommand
        assert AsyncCommand.class.isAssignableFrom(commandClass);
        acceptableCommandClass.put(identifier, commandClass);
    }*/


    @Override
    public void run() {
        assert drupalConnection != null;  // this shouldn't be a problem in runtime.
        drupalConnection.connect();  // if it's not connected yet. make the connection.

        switch (runningMode) {
            case FIRST:
                throw new UnsupportedOperationException("PARALLEL running mode not supported yet.");
                //break;
            case SERIAL:
                runSerial();
                break;
            case PARALLEL:
                throw new UnsupportedOperationException("PARALLEL running mode not supported yet.");
                //break;
            case NONSTOP:
                throw new UnsupportedOperationException("NONSTOP running mode not supported yet.");
                //break;
        }

        // close drupalConnection here is odd because DrupalApp didn't create the connection.
        // whoever create the connection should be responsible closing it.
        drupalConnection.close();
        drupalConnection = null;
        logger.info("Running the DrupalApp is accomplished.");
    }

    protected void runSerial() {
        List<CommandRecord> records = drupalConnection.retrievePendingCommandRecord(this.getIdentifier());
        logger.info("Total number of commands to run: " + records.size());
        logger.fine("Sorting commands.");
        Collections.sort(records);
        for (CommandRecord record : records) {
            try {
                // TODO: might need to set records.start here in order to handle UREC case.
                AsyncCommand command = parseCommand(record);
                logger.info("Executing command: " + record.getCommand());
                command.run();
            } catch (CommandParseException e) {
                logger.severe("Cannot parse command '" + record.getCommand() + "' for application '" + this.getIdentifier() + "'");
                e.printStackTrace();
                record.setStatus(AsyncCommand.Status.UNRECOGNIZED);
            }
            logger.info("Command finished running with status: " + record.getStatus().toString());
            record.persistResult();
        }
    }
}
