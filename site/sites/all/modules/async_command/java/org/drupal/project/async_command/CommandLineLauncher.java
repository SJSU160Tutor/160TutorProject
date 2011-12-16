package org.drupal.project.async_command;

import org.apache.commons.cli.*;
import org.drupal.project.async_command.exception.DrupalConfigException;
import org.drupal.project.async_command.exception.DrupalRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * This handler deals with CommandLineInterface launching DrupalApps.
 * Different launchers (CLI, web, etc) have different interfaces.
 */
public class CommandLineLauncher {

    private static Logger logger = DrupalUtils.getPackageLogger();

    private Class drupalAppClass;
    private GenericDrupalApp drupalApp;
    private File configFile;
    // this is the default running mode.
    private GenericDrupalApp.RunningMode runningMode;

    /**
     * Initialize the launcher with a DrupalApp class.
     *
     * @param drupalAppClass
     */
    public CommandLineLauncher(Class<? extends GenericDrupalApp> drupalAppClass) {
        // assert GenericDrupalApp.class.isAssignableFrom(drupalAppClass);
        this.drupalAppClass = drupalAppClass;
    }


    /**
     * Launches  CLI; users of the class should call this function.
     *
     * @param args arguments from main()
     */
    public void launch(String[] args) {
        logger.info("DrupalApp VERSION: " + DrupalUtils.VERSION);

        // build command parser
        Options options = buildOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine shellCommand = null;
        try {
            shellCommand = parser.parse(options, args);
        } catch (ParseException exp) {
            logger.severe("Cannot parse parameters. Please use -h to see help. Error message: " + exp.getMessage());
            return;
        }

        // get configurable settings. non-exclusive.
        handleSettings(shellCommand);

        constructDrupalApp();

        // handle executable options, mutual exclusive
        handleExecutables(shellCommand);
    }


    private void handleExecutables(CommandLine command) {
        // mutual exclusive options.
        if (command.hasOption('h')) {
            // print help message and exit.
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Drupal application -- " + drupalApp.getIdentifier(), buildOptions());
        }
        else if (command.hasOption('t')) {
            // test connection and exit.
            drupalApp.getDrupalConnection().connect();
            drupalApp.getDrupalConnection().testConnection();
        }
        else if (command.hasOption('e')) {
            // run command directly and exit.
//            initDrupalConnection();
//            prepareApp();
//            String evalStr = command.getOptionValue('e');
//            try {
//                Result result = runCommand(evalStr);
//                String successMsg = result.getStatus() ? "succeeds" : "fails";
//                logger.info("Running " + successMsg + ". Message: " + result.getMessage());
//            } catch (EvaluationFailureException e) {
//                logger.severe("Cannot evaluate script from command line.");
//                throw new DrupalRuntimeException(e);
//            }
        }
        else {
            // run the application command by command. could be time-consuming
            drupalApp.run();
        }
    }

    /**
     * Handle non-executible settings.
     *
     * @param shellCommand
     */
    private void handleSettings(CommandLine shellCommand) {
        if (shellCommand.hasOption('c')) {
            configFile = new File(shellCommand.getOptionValue('c'));
            if (!configFile.exists()) {
                throw new DrupalConfigException("Config file at '" + configFile.getAbsolutePath() + "' does not exist.");
            }
            logger.info("Set configuration file as: " + configFile.getAbsolutePath());
        } else {
            try {
                configFile = DrupalUtils.getConfigPropertiesFile();
            } catch (FileNotFoundException e) {
                throw new DrupalConfigException("Cannot find config.properties under default folders. Use -c to specify the location of config.properties.");
            }
        }



        if (shellCommand.hasOption('r')) {
            String rm = shellCommand.getOptionValue('r');
            try {
                runningMode = GenericDrupalApp.RunningMode.valueOf(rm.toUpperCase());
            } catch (Exception e) {
                throw new DrupalConfigException("Cannot recognize running mode. Use -h to see the options available");
            }
        } else {
            runningMode = GenericDrupalApp.RunningMode.SERIAL;
        }
    }

    private void constructDrupalApp() {
        DrupalConnection drupalConnection = new DrupalConnection(configFile);
        try {
            Constructor<GenericDrupalApp> constructor = drupalAppClass.getConstructor(DrupalConnection.class);
            drupalApp = constructor.newInstance(drupalConnection);
        } catch (NoSuchMethodException e) {
            throw new DrupalRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new DrupalRuntimeException(e);
        } catch (InstantiationException e) {
            throw new DrupalRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new DrupalRuntimeException(e);
        }
        drupalApp.setRunningMode(runningMode);
    }


    /**
     * If subclass needs to set properties, Use config.properties instead of override the method here.
     */
    private Options buildOptions() {
        Options options = new Options();
        options.addOption("c", true, "database configuration file");
        options.addOption("r", true, "program running mode: 'first', 'serial' (default), 'parallel', or nonstop");
        options.addOption("h", "help", false, "print this message");
        options.addOption("t", "test", false, "test connection to Drupal database");
        options.addOption("e", "eval", true, "evaluate a command call directly for this DrupalApp");
        return options;
    }

}
