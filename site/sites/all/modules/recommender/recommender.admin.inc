<?php


function recommender_settings_form() {
  $form = array();
  $rows = db_query("SELECT name, title, cron FROM {recommender_app} ORDER BY name")->fetchAll();
  
  if (empty($rows)) {
    $form['note'] = array(
      '#markup' => t('No recommender modules available. Please install helper modules first. A list of helper modules can be found at !url', 
        array('!url' => l('http://drupal.org/project/recommender', 'http://drupal.org/project/recommender'))),
    );
    return $form;
  }

  // the settings fieldset
  
  $form['settings'] = array(
    '#type' => 'fieldset',
    '#collapsible' => TRUE,
    '#collapsed' => TRUE,
    '#title' => t('Settings'),
    '#description' => t('Configure settings for Recommender API based modules.'),
  );
  
  $cron_options = array(
    'null' => t('Never'),
    '0' => t('Next cron'),
    '1800' => t('Every 30 minutes'),
    '3600' => t('Hourly'),
    '21600' => t('Every 6 hours'),
    '43200' => t('Every 12 hours'),
    '86400' => t('Daily'),
    '259200' => t('Every 3 days'),
    '604800' => t('Weekly')
  );
  
  foreach ($rows as $row) {
    $form['settings']['cron_freq'][$row->name] = array(
      '#title' => $row->title,
      '#description' => t('Please specify cron frequency to run this recommender'),
      '#type' => 'select',
      '#default_value' => $row->cron === NULL ? 'null' : $row->cron,
      '#options' => $cron_options,
    );
  }

  $form['settings']['save'] = array(
    '#type' => 'submit',
    '#value' => t('Save settings'),
    '#name' => 'save'
  );
  

  // the "run" fieldset
  
  $form['run'] = array(
    '#type' => 'fieldset',
    '#collapsible' => TRUE,
    '#collapsed' => FALSE,
    '#title' => t('Run recommender'),
    '#description' => t('Compute recommendations could be time consuming. Please be patient. The actual computation happens on the recommender server. Refer to !doc for more details.',
        array('!doc' => l('the latest README documentation', 'http://drupalcode.org/project/recommender.git/blob_plain/refs/heads/7.x-3.x:/README.html'))),
  );

  $options = array();
  foreach ($rows as $row) {
    $options[$row->name] = $row->title;
  }

  $form['run']['apps'] = array(
    '#title' => t('Choose one or more recommender applications to run now:'),
    '#default_value' => variable_get('recommender_apps', array()),
    '#type' => 'checkboxes',
    //'#description' => t('Please select which recommender to run.'),
    '#options' => $options,
  );

  $form['run']['run'] = array(
    '#type' => 'submit',
    '#value' => t('Run recommender now'),
    '#name' => 'run',
    //'#disabled' => $options == NULL ? TRUE : FALSE,
  );

  // this history fieldset
  $commands_view = views_get_view('recommender_commands');
  $form['history'] = array(
    '#type' => 'fieldset',
    '#collapsible' => FALSE,
    //'#collapsed' => FALSE,
    '#title' => $commands_view->get_title(),
    '#description' => t('Display the list of recommender commands issued.'),
  );

  $form['history']['commands'] = array(
    '#markup' => views_embed_view('recommender_commands'),
  );

  return $form;
}


function recommender_settings_form_submit($form, &$form_state) {
  // save settings
  if ($form_state['clicked_button']['#name'] == 'save') {
    $rows = db_query("SELECT name FROM {recommender_app} ORDER BY name");
    foreach ($rows as $row) {
      $name = $row->name;
      $cron = $form_state['values'][$name];
      if ($cron == 'null') $cron = NULL;
      db_query('UPDATE {recommender_app} SET cron=:cron WHERE name=:name',
        array(':cron' => $cron, ':name' => $name));
    }
    drupal_set_message(t("The settings have been saved."));
  }
  
  // run recommender
  else if ($form_state['clicked_button']['#name'] == 'run') {
    $apps = $form_state['values']['apps'];
    variable_set('recommender_apps', $apps);
    // trigger recommender_run()
    foreach ($apps as $app_name) {
      if (!empty($app_name)) {
        recommender_create_command($app_name);
      }
    }
  }
}


 
