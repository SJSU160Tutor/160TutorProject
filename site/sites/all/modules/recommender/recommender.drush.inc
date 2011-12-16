<?php

/**
 * Implementshook_drush_command().
 */
function recommender_drush_command() {
  $items = array();
  $items['recommender'] = array(
    'description' => "Directly put a request in {async_command} to run recommender asynchronously.",
    'aliases' => array('rec'),
    'arguments' => array(
      'app_name'    => 'Recommender app name to run this command.',
      'description' => 'Description of this command. Enclose spaces in double quotes.',
    ),
    'examples' => array(
      'drush recommender fivestar "Run fivestar recommender"' => "Create a request to run fivestar recommender."
    ),
  );
  return $items;
}

/**
 * Implementation of hook_drush_help().
 */
function recommender_drush_help($section) {
  switch ($section) {
    case 'drush:recommender':
      return dt("Directly put a request in {async_command} to run recommender asynchronously.");
  }
}

function drush_recommender($app_name, $description = NULL) {
  recommender_create_command($app_name, $description);
  // fixme: this doesn't look right: it only retrieves the last execution_id, not the newly created one.
  $rec = recommender_app_load($app_name);
  if ($rec) {
    drush_print("Successfully created run-recommender request with ID: {$rec['execution_id']}");
  }
  else {
    drush_print('Cannot create run-recommender request');
  }
}
