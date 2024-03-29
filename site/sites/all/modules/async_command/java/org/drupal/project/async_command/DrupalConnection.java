package org.drupal.project.async_command;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.drupal.project.async_command.exception.DrupalConfigException;
import org.drupal.project.async_command.exception.DrupalDatabaseException;
import org.drupal.project.async_command.exception.DrupalRuntimeException;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Handles database connection to a Drupal site.  Uses DBCP for connection pooling and DbUtils for simplified JDBC calls.
 */
public class DrupalConnection {

    public static enum DatabaseType {
        MYSQL, POSTGRESQL, UNKNOWN
    }

    /**
     * Database configuration.
     */
    protected Properties config;

    /**
     * Default logger for the whole package.
     */
    protected static Logger logger = DrupalUtils.getPackageLogger();

    // attention: use with caution. don't know if it'll cause problems by providing the datasource to outside programs.
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * JDBC datasource for this Drupal connection.
     */
    protected BasicDataSource dataSource; // we use BasicDataSource rather than the DataSource interface to check the closed property

    /**
     * Drupal table prefix.
     */
    private String dbPrefix;

    /**
     * Batch size to process in JDBC batch update.
     */
    private int maxBatchSize;

    /**
     * Drupal version.
     */
    private int drupalVersion;

    private DatabaseType databaseType;

    public DatabaseType getDatabaseType() {
        return databaseType;
    }


    public static DrupalConnection create() {
        DrupalConnection drupalConnection;
        try {
            File configFile = DrupalUtils.getConfigPropertiesFile();
            drupalConnection = new DrupalConnection(configFile); // OK. we can get a drupalConnection
        } catch (FileNotFoundException e) {
            logger.warning("Cannot find config.properties file. Try settings.php now.");
            try {
                File settingsFile = DrupalUtils.getDrupalSettingsFile();
                Properties config = DrupalUtils.convertSettingsToConfig(settingsFile);
                drupalConnection = new DrupalConnection(config);
            } catch (FileNotFoundException e1) {
                throw new DrupalConfigException("Cannot find either config.properties or settings.php to create DrupalConnection object.");
            }
        }
        return drupalConnection;
    }

    /**
     * Initializes database connection properties, set default properties if not given.
     *
     * @see <a href="http://commons.apache.org/dbcp/configuration.html">DBCP configurations</a>
     * @see <a href="http://dev.mysql.com/doc/refman/5.1/en/connector-j-reference-configuration-properties.html">MySQL configurations</a>
     *
     * @param config Database configuration
     */
    public DrupalConnection(Properties config) {
        DrupalUtils.prepareConfig(config);
        this.config = config;

        drupalVersion = Integer.parseInt(config.getProperty("drupal_version"));

        // set database type.
        String url = this.config.getProperty("url");
        if (url.startsWith("jdbc:mysql")) {
            databaseType = DatabaseType.MYSQL;
        } else if (url.startsWith("jdbc:postgresql")) {
            databaseType = DatabaseType.POSTGRESQL;
        } else {
            databaseType = DatabaseType.UNKNOWN;
        }

        // set Drupal db prefix
        if (config.containsKey("db_prefix")) {
            String p = config.getProperty("db_prefix").trim();
            if (p.length() != 0) {
                dbPrefix = p;
            }
        } else if (config.containsKey("prefix")) {
            // attention: should remove "prefix" later.
            String p = config.getProperty("prefix").trim();
            if (p.length() != 0) {
                dbPrefix = p;
            }
        }

        maxBatchSize = config.containsKey("db_max_batch_size") ? Integer.parseInt(config.getProperty("db_max_batch_size")) : 0;
        if (maxBatchSize > 0) {
            logger.fine("Batch SQL size: " + maxBatchSize);
        }

    }

    /**
     * Initializes database connection properties.
     *
     * @param configString Properties string.
     */
    public DrupalConnection(String configString) {
        this(DrupalUtils.loadProperties(configString));
    }

    /**
     * Initializes database connection properties.
     *
     * @param configFile Properties file to config database connection.
     */
    public DrupalConnection(File configFile) {
        this(DrupalUtils.loadProperties(configFile));
    }



    /**
     * Equivalent to this.connect(false)
     */
    public void connect() {
        connect(false);
    }

    /**
     * Initialize connection pooling and connect to the Drupal database.
     * @param reconnect Whether to force recreate dataSource.
     */
    public void connect(boolean reconnect) {
        if (dataSource != null && !reconnect) {
            return;  // if dataSource already exists and we don't force reconnection, then do just return here.
        }

        try {
            // create data source.
            dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(config);
        } catch (Exception e) {
            logger.severe("Error initializing DataSource for Drupal database connection.");
            throw new DrupalDatabaseException(e);
        }

        // test Drupal connection
        testConnection();
    }

    /**
     * Test if the Drupal connection is working or not. Throws {@see DrupalDatabaseException} if error occurs. Otherwise do nothing.
     */
    public void testConnection() {
        assertConnection();
        try {
            // attention: should use more sophisticated test on user privileges.
            //QueryRunner q = new QueryRunner(dataSource);
            //List<Map<String, Object>> rows = q.query(d("SELECT * FROM {async_command} WHERE app=?"), new MapListHandler(), identifier());
            //for (Map<String, Object> row : rows) {
            //    System.out.println(row.get("id") + ":" + row.get("command"));
            //}
            Connection conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            logger.info("Database connection successful: " + metaData.getDatabaseProductName() + metaData.getDatabaseProductVersion());
            //ResultSet rs = metaData.getColumnPrivileges(null, null, d("{async_command}"), "id");
        } catch (SQLException e) {
            logger.severe("Cannot connect to Drupal database during initialization. Please make sure the Drupal database is up and running and the database access is correct.");
            throw new DrupalDatabaseException(e);
        }
    }

    public void close() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            logger.severe("Cannot close Drupal connection.");
            throw new DrupalDatabaseException(e);
        }
    }

    /**
     * Check if the DataSource connection is still valid or not.
     *
     * @return True if DataSource is still valid, otherwise false.
     */
    public boolean checkConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Throws exception if checkConnection fails.
     */
    private void assertConnection() {
        if (!checkConnection()) {
            throw new DrupalRuntimeException("Drupal database connection not initialized or closed. Please check whether the Drupal database is up and running and the network is fine.");
        }
    }

    /**
     * "Decorates" the SQL statement for Drupal. replace {table} with prefix.
     *
     * @param sql the original SQL statement to be decorated.
     * @return the "decorated" SQL statement
     */
    public String d(String sql) {
        String newSql;
        if (dbPrefix != null) {
            newSql = sql.replaceAll("\\{(.+?)\\}", dbPrefix+"_"+"$1");
        }
        else {
            newSql = sql.replaceAll("\\{(.+?)\\}", "$1");
        }
        logger.fine(newSql);
        return newSql;
    }

    //////////////// queries //////////////


    // attention: later on should add the "connection" version of queries.

    /**
     * Simply run Drupal database queries e.g., query("SELECT nid, title FROM {node} WHERE type=?", "forum");
     *
     * @param sql SQL query to be executed, use {} for table names
     * @param params Parameters to complete the SQL query
     * @return A list of rows as Map (column => value)
     * @throws java.sql.SQLException
     */
    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        assertConnection();
        QueryRunner q = new QueryRunner(dataSource);
        List<Map<String, Object>> result = q.query(d(sql), new MapListHandler(), params);
        return result;
    }

    /**
     * Simply run Drupal database queries, returning one value. e.g. query("SELECT title FROM {node} WHERE nid=?", 1);
     *
     * @param sql SQL query to be executed, use {} for table names
     * @param params Parameters to complete the SQL query
     * @return One value object
     * @throws SQLException
     */
    public Object queryValue(String sql, Object... params) throws SQLException {
        assertConnection();
        QueryRunner q = new QueryRunner(dataSource);
        Object result = q.query(d(sql), new ScalarHandler(), params);
        return result;
    }

    /**
     * Simply run Drupal database queries, returning a list of arrays instead of maps.
     *
     * @param sql SQL query to be executed, use {} for table names
     * @param params Parameters to complete the SQL query
     * @return A list of arrays
     * @throws SQLException
     */
    public List<Object[]> queryArray(String sql, Object... params) throws SQLException {
        assertConnection();
        QueryRunner q = new QueryRunner(dataSource);
        List<Object[]> result = q.query(d(sql), new ArrayListHandler(), params);
        return result;
    }


    ////////////////////// updates //////////////////


    /**
     * Run Drupal database update queries (UPDATE, DELETE, INSERT) without using d(). e.g., query("UPDATE {node} SET sticky=1 WHERE type=?", "forum");
     *
     * @param sql SQL update query to be executed, use {} for table names.
     * @param params parameters to complete the SQL query
     * @return number of rows affected
     * @throws SQLException
     */
    public int update(String sql, Object... params) throws SQLException {
        assertConnection();
        logger.finest("SQL UPDATE: " + sql);
        QueryRunner q = new QueryRunner();
        Connection conn = dataSource.getConnection();
        // basically this is how DbUtils does the update() using DataSource.
        try {
            int num = q.update(conn, d(sql), params);
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return num;
        } finally {
            DbUtils.close(conn);
        }
    }

    /**
     * Insert one record and get the auto-increment ID.
     *
     * @param sql       SQL INSERT statement.
     * @param params    Parameters to complete INSERT
     * @return          auto-increment ID.
     * @throws SQLException
     */
    public long insertAutoIncrement(String sql, Object... params) throws SQLException {
        assertConnection();
        assert(sql.toLowerCase().startsWith("insert"));
        String preparedSql = d(sql);
        logger.finest("SQL INSERT: " + sql);
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(preparedSql, Statement.RETURN_GENERATED_KEYS);
        QueryRunner q = new QueryRunner();

        try {
            q.fillStatement(stmt, params);
            stmt.executeUpdate();
            // get auto generated keys
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            long id = rs.getLong(1);
            // commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return id;
        } finally {
            DbUtils.close(stmt);
            DbUtils.close(conn);
        }
    }

    /**
     * Run batch database update.
     *
     * @param sql       SQL update query to be executed, use {} for table names.
     * @param params    params parameters to complete the SQL query
     * @return          number of rows affected in each batch.
     * @throws SQLException
     */
    public int[] batch(String sql, Object[][] params) throws SQLException {
        assertConnection();
        logger.finest("SQL BATCH: " + sql);
        logger.finest("Number of rows in batch: " + params.length);
        Connection conn = dataSource.getConnection();
        try {
            int[] num = batch(conn, sql, params);
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return num;
        } finally {
            DbUtils.close(conn);
        }
    }

    /**
     * Run batch with maxBatchSize at a time.
     *
     * @param conn      Database connection. Usually given by another batch()
     * @param sql       SQL update query to be executed, use {} for table names.
     * @param params    params params parameters to complete the SQL query
     * @return          Number of rows affected in each batch.
     * @throws SQLException
     */
    private int[] batch(Connection conn, String sql, Object[][] params) throws SQLException {
        QueryRunner q = new QueryRunner();
        String processedSql = d(sql);
        // fix slow problem [#1185100]
        if (maxBatchSize > 0) {
            int start = 0;
            int end = 0;
            int count;
            int[] num = new int[params.length];
            do {
                end += maxBatchSize;
                if (end > params.length) {
                    end = params.length;
                }
                // run batch query
                logger.finest("Database batch processing: " + start + " to " + end);
                int[] batchNum =  q.batch(conn, processedSql, Arrays.copyOfRange(params, start, end));
                for (count=0; count < batchNum.length; count++) {
                    num[start+count] = batchNum[count];
                }
                start = end;
            } while (end < params.length);
            return num;
        } else {
            logger.finest("Batch processing all.");
            return q.batch(conn, processedSql, params);
        }
    }

    /**
     * Java version of Drupal function variable_get(). You need to know the type of the variable in order to change the type of the returned Object
     *
     * @param varName Drupal variable name
     * @return the value of the variable, could be float, integer, string, or PhpObject as PHP array, or null
     * @see org.lorecraft.phparser.SerializedPhpParser
     */
    public Object variableGet(String varName) {
        assertConnection();
        assert varName != null;
        try {
            Object serializedBytes = queryValue("SELECT value FROM {variable} WHERE name=?", varName);
            if (serializedBytes == null) {
                return null;  // variable doesn't exists.
            }
            String serialized = DrupalUtils.convertBlobToString(serializedBytes);
            return DrupalUtils.unserializePhp(serialized);
        } catch (SQLException e) {
            throw new DrupalRuntimeException(e);
        } catch (ClassCastException e) {
            throw new DrupalRuntimeException(e);
        }
    }


    /**
     * Java version of Drupal function variable_set(). Doesn't guarantee to work. Use with Caution.
     *
     * @param varName the variable name.
     * @param varValue the variable value in PHP code string. {@see java.text.MessageFormat}
     */
    public void variableSet(String varName, Object varValue) {
        assertConnection();
        assert varName != null && varValue != null;
        String serialized;
        if (String.class.isInstance(varValue)) {
            // string, boolean, array
            serialized = DrupalUtils.evalPhp("echo serialize(''{0}'');", varValue);
        } else {
            // numeric
            serialized = DrupalUtils.evalPhp("echo serialize({0});", varValue);
        }
        byte[] serializedBytes = serialized.getBytes();
        try {
            update("UPDATE {variable} SET value=? WHERE name=?", serializedBytes, varName);
        } catch (SQLException e) {
            throw new DrupalRuntimeException(e);
        }
    }

    /**
     * Retrieve any records in the {async_command} table given the "where" SQL clause.
     *
     * @param sqlWhere Starts with "WHERE". If null, then returns all records.
     * @return A list of records in the {async_command} table that satisfy the WHERE SQL clause.
     */
    public List<CommandRecord> retrieveAnyCommandRecord(String sqlWhere) {
        assertConnection();
        List<CommandRecord> records = new ArrayList<CommandRecord>();
        List<Map<String, Object>> rows;

        String sql = "SELECT * FROM {async_command}";
        if (sqlWhere != null) {
            assert sqlWhere.toUpperCase().startsWith("WHERE");
            sql = sql + " " + sqlWhere;
        }
        try {
            rows = query(sql);
        } catch (SQLException e) {
            throw new DrupalDatabaseException(e);
        }

        for (Map<String, Object> row : rows) {
            CommandRecord record = new CommandRecord(row, this);
            records.add(record);
        }
        return records;
    }

    /**
     * Retrieve a list of pending commands for the given app.
     *
     * @param appName
     * @return
     */
    public List<CommandRecord> retrievePendingCommandRecord(String appName) {
        assert appName != null;
        logger.finest("Retrieving pending commands for " + appName);
        StringBuffer sqlWhere = new StringBuffer();
        sqlWhere.append("WHERE app='").append(appName).append("' AND (status IS NULL OR status='").append(AsyncCommand.Status.PENDING.toString()).append("')");
        return retrieveAnyCommandRecord(sqlWhere.toString());
    }

    /**
     * NOT SUPPORTED YET! Insert a command record into the database using fields.
     *
     * @param fields fields data eg field=>value.
     * @return ID of the newly created record.
     */
    public long insertCommandRecord(Map<String, Object> fields) {
        // fixme: implement. duplicate code from Drupal PHP version?
        throw new UnsupportedOperationException();
    }


    /**
     * Load a record from database
     * .
     * @param id Record id from the database.
     * @return The record object.
     */
    public CommandRecord retrieveCommandRecord(long id) {
        try {
            List<Map<String, Object>> rows = query("SELECT * FROM {async_command} WHERE id = ?", id);
            assert rows.size() == 1;
            return new CommandRecord(rows.get(0), this);
        } catch (SQLException e) {
            throw new DrupalDatabaseException(e);
        }
    }

    /**
     * Get a Drupal database connection from the pool. Caller is responsible to close it (Note: not sure if it'll be managed by ConnectionPooling)
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        assertConnection();
        return dataSource.getConnection();
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }
}
