package org.drupal.project.async_command;

import org.apache.commons.lang3.StringUtils;
import org.drupal.project.async_command.exception.DrupalConfigException;
import org.drupal.project.async_command.exception.DrupalRuntimeException;
import org.drupal.project.async_command.exception.EvaluationFailureException;
import org.lorecraft.phparser.SerializedPhpParser;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * A group of useful tools to help with Drupal apps.
 * Note: You can potentially run any Drupal API using executeDrush() with "drush eval". Make sure you have a local copy of Drupal, and set its database to the Drupal database you try to connect to.
 */
public final class DrupalUtils {

    private static Logger logger = getPackageLogger();

    /**
     * Version string to keep track of jar changes.
     */
    public final static String VERSION = "7_1_1";

    /**
     * @return Default logger for the whole package.
     */
    public static Logger getPackageLogger() {
        return Logger.getLogger("org.drupal.project.async_command");
    }

    /**
     * Evaluates PHP code and return the output.
     * TODO: Use JSR 223 instead of using direct CLI.
     *
     * @param phpCode PHP code snippet.
     * @return PHP code execution output.
     */
    public static String evalPhp(String phpCode) {
        // retrieve php cli executable
        // TODO: test with 'php -v' before executing the command.
        String phpCli = System.getenv("PHP_EXEC");
        if (phpCli == null) {
            logger.warning("Please set PHP_EXEC. Use default php executable instead.");
            phpCli = "php";
        }
        String[] cmd = {phpCli,  "-r", phpCode};

        // TODO: use executeSystemCommand() instead.
        try {
            // run the command
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            if (process.exitValue() != 0) {
                logger.severe("PHP process exception.");
                throw new DrupalRuntimeException("Unexpected PHP error: " + process.exitValue());
            }
            // read output
            return getContent(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            logger.severe("Cannot run PHP code. Possible reasons: missing PHP CLI executable or dependent libraries.");
            throw new DrupalRuntimeException(e);
        } catch (InterruptedException e) {
            logger.severe("PHP code interrupted.");
            throw new DrupalRuntimeException(e);
        }
    }

    /**
     * Execute PHP using substitutes. Be very careful of using single quote in pattern. Needs to use two single quotes for PHP string.
     * @see java.text.MessageFormat
     *
     * @param pattern PHP code pattern
     * @param params Values to insert into the PHP code pattern
     * @return PHP execute output.
     */
    public static String evalPhp(String pattern, Object... params) {
        assert (pattern!=null && !pattern.isEmpty());
        // generate PHP code following java.text.MessageFormat.format()
        String phpCode = MessageFormat.format(pattern, params);
        //System.out.println(phpCode);
        return evalPhp(phpCode);
    }

    /**
     * Unserialize PHP array string
     *
     * @param serialized The string of the PHP serialized array
     * @return unserialized PHP array as a Map.
     */
    public static Map<String, Object> unserializePhpArray(String serialized) {
 		return (Map<String, Object>) unserializePhp(serialized);
    }

    /**
     * Unserialize any PHP variable.
     *
     * @param serialized The string of the PHP serialized array
     * @return The PHP object.
     * @see org.lorecraft.phparser.SerializedPhpParser
     */
    public static Object unserializePhp(String serialized) {
        SerializedPhpParser serializedPhpParser = new SerializedPhpParser(serialized);
        return serializedPhpParser.parse();
    }

    /**
     * From the input reader and get all its content.
     *
     * @param input input reader
     * @return the content of the reader in String.
     * @throws IOException
     */
    public static String getContent(Reader input) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = input.read()) != -1) {
            sb.append((char)c);
        }
        return sb.toString();
    }

    /**
     * Convert the byte[] blog value from this.queryValue() to a string. Might not work for all cases.
     *
     * @param blobValue a byte[] array
     * @return a string of the blob value
     */
    public static String convertBlobToString(Object blobValue) {
        //assert blobValue.getClass().isInstance((byte[]).getClass());
        byte[] blobBytes = (byte[]) blobValue;
        return new String(blobBytes);
    }


    /**
     * Try to get Drupal's settings.php file in various locations.
     *
     * @return File The settings.php
     * @exception FileNotFoundException
     */
    public static File getDrupalSettingsFile() throws FileNotFoundException {
        return locateFile("settings.php");
    }

    /**
     * Find the config.properties file under various locations.
     *
     * @return config.properties file
     * @throws FileNotFoundException
     */
    public static File getConfigPropertiesFile() throws FileNotFoundException {
        return locateFile("config.properties");
    }

    /**
     * Try to locate a given file. Refer to drush/includes/environment.inc => drush_site_path().
     * For now, we assume the DrupalApp jar is under 'sites/all/modules', and we find the file at 'sites/default/' or current working directory.
     * Make symbol links could help locate the file? We won't handle other more sophisticated cases.
     *
     * @param fileName The file name to be located.
     * @return The file if found, otherwise throw exception.
     * @throws FileNotFoundException
     */
    private static File locateFile(String fileName) throws FileNotFoundException {
        //StringBuilder path = new StringBuilder();
        //String jarDir = getClass().getResource("").getPath();
        // this doesn't work if the folders are symbolic links.
        //path.append(jarDir).append("..").append(File.separator).append("..").append(File.separator).append("default").append(File.separator).append(fileName);
        //return path.toString();

        File jarDir = new File(DrupalUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()); // this is URL path.
        String jarDirStr = jarDir.getAbsolutePath(); // this is the file system path.
        logger.fine("AsyncCommand jar file location: " + jarDirStr);

        File drupalHome = null;
        try {
            drupalHome = getFileFromEnv("DRUPAL_HOME");
        } catch (FileNotFoundException e) {}

        File theFile;
        String identityStr = "sites" + File.separator + "all" + File.separator + "modules" + File.separator;
        int pos = jarDirStr.indexOf(identityStr);
        if (pos != -1) {
            theFile = new File(jarDirStr.substring(0, pos) + "sites" + File.separator + "default" + File.separator + fileName);
        } else {
            // try find the file under current directory.
            String workingDir = System.getProperty("user.dir");
            theFile = new File(workingDir + File.separator + fileName);
        }

        if (theFile.exists()) {
            logger.info("Found file '" + fileName + "' at: " + theFile.getAbsolutePath());
            return theFile;
        } else {
            throw new FileNotFoundException("Cannot find file '" + fileName + "' under the current working directory or sites/default");
        }
    }

    /**
     * Prepare config properties, e.g. make driverClassName based on database_type, etc.
     *
     * @param config The original, un-prepared config (will be prepared after execution)
     */
    public static void prepareConfig(Properties config) {
        // handle driverClassName. If database_type is not mysql or pgsql, you should set driverClassName directly.
        if (!config.containsKey("driverClassName") && config.containsKey("database_type")) {
            String databaseType = config.getProperty("database_type").toLowerCase();
            if (databaseType.equals("mysql")) {
                config.setProperty("driverClassName", "com.mysql.jdbc.Driver");
            } else if (databaseType.equals("postgresql")) {
                config.setProperty("driverClassName", "org.postgresql.Driver");
            }
        }

        // handle url
        if (!config.containsKey("url") && config.containsKey("database_type") && config.containsKey("database_name") && config.containsKey("host_name")) {
            StringBuffer url = new StringBuffer();
            url.append("jdbc:").append(config.getProperty("database_type").toLowerCase()).append("://").append(config.getProperty("host_name"));
            if (config.containsKey("host_port")) {
                url.append(":").append(config.getProperty("host_port"));
            }
            url.append('/').append(config.getProperty("database_name"));
            config.setProperty("url", url.toString());
        }

        // test required properties
        if (!(config.containsKey("username") && config.containsKey("password") && config.containsKey("driverClassName") && config.containsKey("url") && config.containsKey("drupal_version"))) {
            throw new DrupalConfigException("Missing required configuration parameters (username, password, driverClassName, url and drupal_version)");
        }

        // set some default properties
        if (!config.containsKey("defaultAutoCommit")) {
            config.setProperty("defaultAutoCommit", "false");
        }

        //if (!config.containsKey("defaultTransactionIsolation")) {
        //    config.setProperty("defaultTransactionIsolation", "1");
        //}
    }

    /**
     * Make config properties out of Drupal settings.php. You can use {@see #getDrupalSettingsFile} to locate settings.php.
     * Often, you need to run {@see #prepareConfig} to further process the config. This method only does the raw conversion.
     *
     * @param settingsFile The settings.php file
     * @return config properties.
     */
    public static Properties convertSettingsToConfig(File settingsFile) {
        String phpCode = "include '" + settingsFile.getAbsolutePath() + "'; echo serialize($databases['default']['default']);";
        String dbSettingsStr = evalPhp(phpCode);
        Properties config = new Properties();

        //logger.info(dbSettingsStr);
        Map<String, Object> dbSettings = unserializePhpArray(dbSettingsStr);
        if (!(dbSettings.containsKey("username") && dbSettings.containsKey("password") && dbSettings.containsKey("driver") && dbSettings.containsKey("host") && dbSettings.containsKey("database"))) {
            logger.severe("Can't find necessary database info. Content: " + dbSettingsStr);
            throw new DrupalRuntimeException("Can't find necessary database info.");
        }

        config.put("username", dbSettings.get("username"));
        config.put("password", dbSettings.get("password"));
        config.put("db_prefix", dbSettings.get("prefix"));

        String driverName = ((String) dbSettings.get("driver")).toLowerCase();
        if (driverName.startsWith("mysql")) {
            config.put("database_type", "mysql");
        } else if (driverName.startsWith("pgsql")) {
            config.put("database_type", "postgresql");
        } else {
            throw new DrupalRuntimeException("Only support MySQL and PostgreSQL for now. Please use the config.properties file if you use other DBMS.");
        }

        config.put("host_name", dbSettings.get("host"));
        if (dbSettings.containsKey("port") && !dbSettings.get("port").equals("")) {
            config.put("host_port", dbSettings.get("port"));
        }
        config.put("database_name", dbSettings.get("database"));

        // then handle cryption settings
        phpCode = "include '" + settingsFile.getAbsolutePath() + "'; echo isset($mcrypt_secret_key) ? $mcrypt_secret_key : substr($drupal_hash_salt, 0, 6);";
        String secretKey = evalPhp(phpCode);
        config.put("mcrypt_secret_key", secretKey);

        // put drupal version into it.
        config.put("drupal_version", '7');

        return config;
    }

    /**
     * Helper function to load properties from a String.
     *
     * @param configString
     * @return
     */
    public static Properties loadProperties(String configString) {
        Properties config = new Properties();
        try {
            config.load(new StringReader(configString));
        } catch (IOException e) {
            throw new DrupalRuntimeException("Cannot read config string.");
        }
        return config;
    }

    /**
     * Helper function to load properties from a config file.
     *
     * @param configFile
     * @return
     */
    public static Properties loadProperties(File configFile) {
        Properties config = new Properties();
        try {
            config.load(new FileReader(configFile));
        } catch (IOException e) {
            throw new DrupalRuntimeException("Cannot read config file at " + configFile.getAbsolutePath());
        }
        return config;
    }

    /**
     * Get the unix timestamp of the local server.
     * @return
     */
    public static long getLocalUnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Get the long value from any Object, if possible.
     *
     * @param value The object that could either be null, or int, or string.
     * @return  The long value of the "value".
     */
    public static Long getLong(Object value) {
        if (value == null) {
            return null;
        } else if (Integer.class.isInstance(value)) {
            return ((Integer) value).longValue();
        } else if (Long.class.isInstance(value)) {
            return (Long) value;
        } else if (String.class.isInstance(value)) {
            return Long.valueOf((String) value);
        } else {
            throw new IllegalArgumentException("Cannot parse value: " + value.toString());
        }
    }


    private static File getFileFromEnv(String env) throws FileNotFoundException {
        String filePath = System.getenv(env);
        if (filePath == null) {
            throw new FileNotFoundException("Please specify system environment variable: " + env);
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot find file for " + env + ": " + filePath);
        }
        return file;
    }

    /**
     * Execute drush using the given parameters. You have to set DRUPAL_HOME and DRUSH_HOME before executing this command.
     * Note: You can potentially run any Drupal API using executeDrush() with "drush eval".
     * Make sure you have a local copy of Drupal, and set its database to the Drupal database you try to connect to.
     *
     * @param vars Parameters for the drush execution.
     * @return The output of drush execution.
     */
    public static String executeDrush(String... vars) throws EvaluationFailureException {
        try {
            File drupalHome = getFileFromEnv("DRUPAL_HOME");
            if (!drupalHome.isDirectory()) {
                throw new FileNotFoundException("Please set DRUPAL_HOME correctly.");
            }
            File drushHome = getFileFromEnv("DRUSH_HOME");
            if (!drushHome.isDirectory()) {
                throw new FileNotFoundException("Please set DRUSH_HOME correctly.");
            }

            String drushExecutable = drushHome.getAbsolutePath() + File.separator + "drush";
            List<String> commands = new ArrayList<String>();
            commands.add(drushExecutable);
            commands.addAll(Arrays.asList(vars));
            return executeSystemCommand(commands, drupalHome);

        } catch (FileNotFoundException e) {
            throw new DrupalConfigException(e);
        }
    }

    /**
     * Execute a command in the working dir, and return the output as a String. If error, log the errors in logger.
     * TODO: potentially use commons-exec instead.
     *
     * @param command The list of command and parameters.
     * @param workingDir The working directory. Could be null. The it's default user.dir.
     * @return command output.
     * @throws EvaluationFailureException
     */
    public static String executeSystemCommand(List<String> command, File workingDir) throws EvaluationFailureException {
        logger.finest("Running system command: " + StringUtils.join(command, ' '));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (workingDir != null && workingDir.exists() && workingDir.isDirectory()) {
            processBuilder.directory(workingDir);
        } else {
            logger.info("Using current user directory to run system command.");
        }

        try {
            Process process = processBuilder.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                logger.severe(getContent(new InputStreamReader(process.getErrorStream())));
                throw new EvaluationFailureException("Unexpected error executing system command: " + process.exitValue());
            } else {
                // running successfully.
                return getContent(new InputStreamReader(process.getInputStream()));
            }
        } catch (IOException e) {
            throw new EvaluationFailureException(e);
        } catch (InterruptedException e) {
            throw new EvaluationFailureException(e);
        }
    }

    /**
     * Get either the identifier if presented, or the class name.
     * @param classObject
     * @return
     */
    public static String getIdentifier(Class<?> classObject) {
        Identifier id = classObject.getAnnotation(Identifier.class);
        if (id != null) {
            return id.value();
        } else {
            return classObject.getSimpleName();
        }
    }

}
