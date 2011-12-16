package org.drupal.project.async_command.test;

import org.drupal.project.async_command.*;
import org.drupal.project.async_command.exception.EvaluationFailureException;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.Assert.assertTrue;

public class DefaultDrupalAppTest {

    @Test
    public void testPingMe() throws EvaluationFailureException, SQLException {
        // create a pingme command.
        // attention: this drupal connection is the one Drush is using. could be different from the one in the drupalConnection created earlier
        String output = DrupalUtils.executeDrush("async-command", "default", "PingMe", "From UnitTest", "string1=Hello");
        System.out.println("Drush output: " + output);

        DrupalConnection drupalConnection = DrupalConnection.create();
        drupalConnection.connect();
        Long id = DrupalUtils.getLong(drupalConnection.queryValue("SELECT max(id) FROM {async_command}"));
        DefaultDrupalApp drupalApp = new DefaultDrupalApp(drupalConnection);
        drupalApp.run();

        // run() closes the drupal connection, so we recreate again.
        drupalConnection.connect(true);
        assertTrue(output.trim().endsWith(id.toString()));
        CommandRecord record = drupalConnection.retrieveCommandRecord(id);
        assertTrue(record.getStatus().equals(AsyncCommand.Status.SUCCESS));
        assertTrue(record.getMessage().endsWith("Hello"));
    }
}
