<?php
/**
 * @file recommender.views.inc
 * Views support for recommender tables
 */


/**
 * Implements hook_views_data().
 */
function recommender_views_data() {
  $data = array(
    // for the recommender_app table
    'recommender_app' => array(
      'table' => array(
        'group' => t('Recommender Application'),
        'join' => array(
          'recommender_similarity' => array(
            'left_field' => 'app_id',
            'field' => 'id',
            'type' => 'inner',
          ),
          'recommender_prediction' => array(
            'left_field' => 'app_id',
            'field' => 'id',
            'type' => 'inner',
          ),
          'async_command' => array(
            'left_field' => 'id1',
            'field' => 'id',
            //'type' => 'left', // this is default
          ),
        ),
      ),

      'name' => array(
        'title' => t('Application name'),
        'help' => t('Recommender application used to provide similarity/prediction data. Filtering is a little slower than using application id.'),
        'field' => array(
          'handler' => 'views_handler_field',
        ),
        'filter' => array(
          'handler' => 'views_handler_filter_recommender_app_name',
        ),
      ),

      'title' => array(
        'title' => t('Application human readable name'),
        'help' => t('Human readable name of the recommender application.'),
        'field' => array(
          'handler' => 'views_handler_field',
        ),
      ),
    ),


    // for the recommender_similarity table
    'recommender_similarity' => array(
      'table' => array(
        'group' => t('Recommender Similarity'),
        'help' => t('Recommender similarity data between node-node and user-user'),
        /*'join' => array(
          'node' => array(
            'left_field' => 'nid',
            'field' => 'target_eid',
            'type' => 'inner',
            //'handler'
          ),
          'users' => array(
            'left_field' => 'uid',
            'field' => 'target_eid',
            'type' => 'inner',
          ),
        ),*/

        // [#1191904] use base tables
        'base' => array(
          'field' => 'id',
          'title' => t('Recommender Similarity'),
          'help' =>  t('Recommender similarity data between node-node and user-user'),
        ),
      ),

      'app_id' => array(
        'title' => t('Application ID'),
        'help' => t('Recommender application used to provide similarity data'),
        'filter' => array(
          'handler' => 'views_handler_filter_recommender_app_id',
        ),
      ),

      'source_eid' => array(
        'title' => t('Entity ID (Source)'),
        'help' => t('The entity ID (node, user, etc) upon which this similarity is for.'),
        'argument' => array(
          'handler' => 'views_handler_argument_numeric',
        ),
        // TODO: we don't have a field handler. Later on we can add a "Similar to..." Field
        // But there might not be a need.
      ),

      // // [#1191904] use base tables
      'target_eid' => array(
        'title' => t('Entity ID (Target)'),
        'help' => t('The entity ID (node, user, etc) similar to the source entity.'),
        'relationship' => array(
          'base' => 'node',
          'base field' => 'nid',
          'handler' => 'views_handler_relationship_recommender_entity_type',
          'label' => t('Recommended entity (by similarity)'),
        ),
      ),

      'score' => array(
        'title' => t('Similarity score'),
        'help' => t('Score of how similar the source entity and the target entity are.'),
        'field' => array(
          'handler' => 'views_handler_field_numeric',
          'click sortable' => TRUE,
        ),
        'filter' => array(
          'handler' => 'views_handler_filter_numeric',
        ),
        'sort' => array(
          'handler' => 'views_handler_sort',
        ),
      ),

      'updated' => array(
        'title' => t('Updated'),
        'help' => t('Last updated timestamp of this similarity score.'),
        'field' => array(
          'handler' => 'views_handler_field_date',
          'click sortable' => TRUE,
        ),
        'filter' => array(
          'handler' => 'views_handler_filter_date',
        ),
        'sort' => array(
          'handler' => 'views_handler_sort_date',
        ),
      ),
    ),

    // for the recommender_prediction table
    'recommender_prediction' => array(
      'table' => array(
        'group' => t('Recommender Prediction'),
        'help' => t('Recommender prediction data between user-node.'),
        /*'join' => array(
          'node' => array(
            'left_field' => 'nid',
            'field' => 'target_eid',
            'type' => 'inner',
          ),
          'users' => array(
            'left_field' => 'uid',
            'field' => 'target_eid',
            'type' => 'inner',
          ),
        ),*/

        // [#1191904] use base tables
        'base' => array(
          'field' => 'id',
          'title' => t('Recommender Prediction'),
          'help' =>  t('Recommender prediction data for node-user pairs.'),
        ),
      ),

      'app_id' => array(
        'title' => t('Application ID'),
        'help' => t('Recommender application used to provide prediction data'),
        'filter' => array(
          'handler' => 'views_handler_filter_recommender_app_id',
        ),
      ),

      'source_eid' => array(
        'title' => t('Entity ID (Source)'),
        'help' => t('The entity ID (node, user, etc) upon which this prediction is for.'),
        'argument' => array(
          'handler' => 'views_handler_argument_numeric',
        ),
      ),

      // [#1191904] use base tables
      'target_eid' => array(
        'title' => t('Entity ID (Target)'),
        'help' => t('The entity ID (node, user, etc) recommended to the source entity.'),
        'relationship' => array(
          'base' => 'node',
          'base field' => 'nid',
          'handler' => 'views_handler_relationship_recommender_entity_type',
          'label' => t('Recommended entity (by prediction)'),
        ),
      ),

      'score' => array(
        'title' => t('Prediction score'),
        'help' => t('Score of how confident to recommend the target entity to the source entity'),
        'field' => array(
          'handler' => 'views_handler_field_numeric',
          'click sortable' => TRUE,
        ),
        'filter' => array(
          'handler' => 'views_handler_filter_numeric',
        ),
        'sort' => array(
          'handler' => 'views_handler_sort',
        ),
      ),

      'updated' => array(
        'title' => t('Updated'),
        'help' => t('Last updated timestamp of this prediction score.'),
        'field' => array(
          'handler' => 'views_handler_field_date',
          'click sortable' => TRUE,
        ),
        'filter' => array(
          'handler' => 'views_handler_filter_date',
        ),
        'sort' => array(
          'handler' => 'views_handler_sort_date',
        ),
      ),
    ),
  );

  return $data;
}



/**
 * Filter by recommender application id
 */
class views_handler_filter_recommender_app_id extends views_handler_filter_in_operator {
  function get_value_options() {
    if (isset($this->value_options)) {
      return;
    }

    $this->value_options = array();
    $apps = db_query('SELECT id, title FROM {recommender_app}');
    foreach ($apps as $app) {
      $this->value_options[$app->id] = check_plain($app->title);
    }
  }
}

/**
 * Filter by recommender application name
 */
class views_handler_filter_recommender_app_name extends views_handler_filter_in_operator {
  function get_value_options() {
    if (isset($this->value_options)) {
      return;
    }

    $this->value_options = array();
    $apps = db_query('SELECT name, title FROM {recommender_app}');
    foreach ($apps as $app) {
      $this->value_options[$app->name] = check_plain($app->title);
    }
  }
}

/**
 * Argument by entity type [#1191904]
 */
class views_handler_relationship_recommender_entity_type extends views_handler_relationship {

  function init(&$view, &$options) {
    parent::init($view, $options);
    // change the base table and base table field based on user input.
    $base_type = isset($options['base_entity_type']) ? $options['base_entity_type'] : 'node';
    $base_table = views_fetch_data($base_type);
    $this->definition['base'] = $base_type;
    $this->definition['base field'] = $base_table['table']['base']['field'];
  }

  function option_definition() {
    $options = parent::option_definition();
    $options['base_entity_type'] = array('default' => 'node');
    return $options;
  }

  function options_form(&$form, &$form_state) {
    parent::options_form($form, $form_state);

    $base_tables = views_fetch_base_tables();
    $entity_options = array();
    foreach ($base_tables as $name => $table) {
      $entity_options[$name] = $table['title'];
    }

    $form['base_entity_type'] = array(
      '#type' => 'select',
      '#title' => t('Entity type (base table)'),
      '#default_value' => isset($this->options['base_entity_type']) ? $this->options['base_entity_type'] : 'node',
      '#options' => $entity_options,
      '#description' => t('Select the entity type (node, user, etc) of the recommended items.'),
    );
  }
}
