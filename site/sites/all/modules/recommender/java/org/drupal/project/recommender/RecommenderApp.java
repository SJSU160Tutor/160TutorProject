package org.drupal.project.recommender;

import org.drupal.project.async_command.CommandLineLauncher;
import org.drupal.project.async_command.DrupalConnection;
import org.drupal.project.async_command.GenericDrupalApp;
import org.drupal.project.async_command.Identifier;

/**
 * DrupalApp that runs recommender.
 */
@Identifier("recommender")
public class RecommenderApp extends GenericDrupalApp {

    /**
     * Register acceptable AsyncCommand classes in constructor.
     *
     * @param drupalConnection Connection to a Drupal database that has the {async_command} table.
     */
    public RecommenderApp(DrupalConnection drupalConnection) {
        super(drupalConnection);
        registerCommandClass(RunRecommender.class);
    }

    public static void main(String[] args) {
        CommandLineLauncher launcher = new CommandLineLauncher(RecommenderApp.class);
        launcher.launch(args);
    }
}
