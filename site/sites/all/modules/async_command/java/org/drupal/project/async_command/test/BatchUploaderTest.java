package org.drupal.project.async_command.test;

import org.drupal.project.async_command.BatchUploader;
import org.drupal.project.async_command.DrupalConnection;
import org.drupal.project.async_command.DrupalUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static junit.framework.Assert.assertEquals;

public class BatchUploaderTest {

    private DrupalConnection drupalConnection;

    @Before
    public void setUp() throws Exception {
        drupalConnection = DrupalConnection.create();
        drupalConnection.connect();
        drupalConnection.update("CREATE TABLE {async_command_batch_updater} (id INT(10) NOT NULL)");
    }

    @Test
    public void testBatchUpdater() throws Exception {
        Connection connection = drupalConnection.getConnection();
        BatchUploader up = new BatchUploader(null, "TestUploader", connection, drupalConnection.d("INSERT INTO {async_command_batch_updater} VALUES(?)"), 10);
        up.start();
        for (int i = 0; i < 100; i++) {
            up.put(i);
        }
        up.accomplish();
        System.out.println("Finished adding data to database.");
        up.join();
        System.out.println("Finished uploading.");
        long n1 = DrupalUtils.getLong(drupalConnection.queryValue("SELECT count(*) FROM {async_command_batch_updater}"));
        assertEquals(n1, 100L);
        long n2 = DrupalUtils.getLong(drupalConnection.queryValue("SELECT count(*) FROM {async_command_batch_updater} WHERE id < 50"));
        assertEquals(n2, 50L);
    }


    @After
    public void tearDown() throws Exception {
        drupalConnection.update("DROP TABLE {async_command_batch_updater}");
    }
}
