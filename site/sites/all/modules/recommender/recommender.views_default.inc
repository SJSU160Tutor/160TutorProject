<?php

/**
 * Implements hook_views_default_views().
 */
function recommender_views_default_views() {
  $view = _recommender_commands_view();
  $views = array (
    $view->name => $view,
  );
  return $views;
}

// directly copied from view export.
function _recommender_commands_view() {
  $view = new view;
  $view->name = 'recommender_commands';
  $view->description = 'Shows all recommender commands';
  $view->tag = 'async_command';
  $view->base_table = 'async_command';
  $view->human_name = 'Recommender commands';
  $view->core = 7;
  $view->api_version = '3.0-alpha1';
  $view->disabled = FALSE; /* Edit this to true to make a default view disabled initially */

  /* Display: Master */
  $handler = $view->new_display('default', 'Master', 'default');
  $handler->display->display_options['title'] = 'Recommender commands';
  $handler->display->display_options['access']['type'] = 'none';
  $handler->display->display_options['cache']['type'] = 'none';
  $handler->display->display_options['query']['type'] = 'views_query';
  $handler->display->display_options['query']['options']['query_comment'] = FALSE;
  $handler->display->display_options['exposed_form']['type'] = 'basic';
  $handler->display->display_options['pager']['type'] = 'full';
  $handler->display->display_options['pager']['options']['items_per_page'] = '10';
  $handler->display->display_options['pager']['options']['offset'] = '0';
  $handler->display->display_options['pager']['options']['id'] = '0';
  $handler->display->display_options['pager']['options']['expose']['items_per_page_options_all'] = 0;
  $handler->display->display_options['style_plugin'] = 'table';
  /* Relationship: Async Command: Uid */
  $handler->display->display_options['relationships']['uid']['id'] = 'uid';
  $handler->display->display_options['relationships']['uid']['table'] = 'async_command';
  $handler->display->display_options['relationships']['uid']['field'] = 'uid';
  $handler->display->display_options['relationships']['uid']['required'] = 0;
  /* Relationship: Async Command: Eid */
  $handler->display->display_options['relationships']['eid']['id'] = 'eid';
  $handler->display->display_options['relationships']['eid']['table'] = 'async_command';
  $handler->display->display_options['relationships']['eid']['field'] = 'eid';
  $handler->display->display_options['relationships']['eid']['label'] = 'Node';
  $handler->display->display_options['relationships']['eid']['required'] = 0;
  /* Field: Async Command: ID */
  $handler->display->display_options['fields']['id']['id'] = 'id';
  $handler->display->display_options['fields']['id']['table'] = 'async_command';
  $handler->display->display_options['fields']['id']['field'] = 'id';
  /* Field: Async Command: Description */
  $handler->display->display_options['fields']['description']['id'] = 'description';
  $handler->display->display_options['fields']['description']['table'] = 'async_command';
  $handler->display->display_options['fields']['description']['field'] = 'description';
  $handler->display->display_options['fields']['description']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['description']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['description']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['description']['alter']['external'] = 0;
  $handler->display->display_options['fields']['description']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['description']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['description']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['description']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['description']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['description']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['description']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['description']['alter']['html'] = 0;
  $handler->display->display_options['fields']['description']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['description']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['description']['hide_empty'] = 0;
  $handler->display->display_options['fields']['description']['empty_zero'] = 0;
  $handler->display->display_options['fields']['description']['hide_alter_empty'] = 0;
  /* Field: User: Name */
  $handler->display->display_options['fields']['name']['id'] = 'name';
  $handler->display->display_options['fields']['name']['table'] = 'users';
  $handler->display->display_options['fields']['name']['field'] = 'name';
  $handler->display->display_options['fields']['name']['relationship'] = 'uid';
  $handler->display->display_options['fields']['name']['label'] = 'User';
  $handler->display->display_options['fields']['name']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['name']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['name']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['name']['alter']['external'] = 0;
  $handler->display->display_options['fields']['name']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['name']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['name']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['name']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['name']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['name']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['name']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['name']['alter']['html'] = 0;
  $handler->display->display_options['fields']['name']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['name']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['name']['empty'] = 'N/A';
  $handler->display->display_options['fields']['name']['hide_empty'] = 0;
  $handler->display->display_options['fields']['name']['empty_zero'] = 0;
  $handler->display->display_options['fields']['name']['hide_alter_empty'] = 1;
  $handler->display->display_options['fields']['name']['link_to_user'] = 1;
  $handler->display->display_options['fields']['name']['overwrite_anonymous'] = 1;
  $handler->display->display_options['fields']['name']['anonymous_text'] = 'N/A';
  /* Field: Async Command: Created */
  $handler->display->display_options['fields']['created']['id'] = 'created';
  $handler->display->display_options['fields']['created']['table'] = 'async_command';
  $handler->display->display_options['fields']['created']['field'] = 'created';
  $handler->display->display_options['fields']['created']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['created']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['created']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['created']['alter']['external'] = 0;
  $handler->display->display_options['fields']['created']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['created']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['created']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['created']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['created']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['created']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['created']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['created']['alter']['html'] = 0;
  $handler->display->display_options['fields']['created']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['created']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['created']['hide_empty'] = 0;
  $handler->display->display_options['fields']['created']['empty_zero'] = 0;
  $handler->display->display_options['fields']['created']['hide_alter_empty'] = 0;
  $handler->display->display_options['fields']['created']['date_format'] = 'time ago';
  /* Field: Async Command: Start timestamp */
  $handler->display->display_options['fields']['start']['id'] = 'start';
  $handler->display->display_options['fields']['start']['table'] = 'async_command';
  $handler->display->display_options['fields']['start']['field'] = 'start';
  $handler->display->display_options['fields']['start']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['start']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['start']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['start']['alter']['external'] = 0;
  $handler->display->display_options['fields']['start']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['start']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['start']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['start']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['start']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['start']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['start']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['start']['alter']['html'] = 0;
  $handler->display->display_options['fields']['start']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['start']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['start']['hide_empty'] = 0;
  $handler->display->display_options['fields']['start']['empty_zero'] = 0;
  $handler->display->display_options['fields']['start']['hide_alter_empty'] = 0;
  $handler->display->display_options['fields']['start']['date_format'] = 'short';
  /* Field: Async Command: End timestamp */
  $handler->display->display_options['fields']['end']['id'] = 'end';
  $handler->display->display_options['fields']['end']['table'] = 'async_command';
  $handler->display->display_options['fields']['end']['field'] = 'end';
  $handler->display->display_options['fields']['end']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['end']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['end']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['end']['alter']['external'] = 0;
  $handler->display->display_options['fields']['end']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['end']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['end']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['end']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['end']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['end']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['end']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['end']['alter']['html'] = 0;
  $handler->display->display_options['fields']['end']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['end']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['end']['hide_empty'] = 0;
  $handler->display->display_options['fields']['end']['empty_zero'] = 0;
  $handler->display->display_options['fields']['end']['hide_alter_empty'] = 0;
  $handler->display->display_options['fields']['end']['date_format'] = 'short';
  /* Field: Async Command: Status */
  $handler->display->display_options['fields']['status']['id'] = 'status';
  $handler->display->display_options['fields']['status']['table'] = 'async_command';
  $handler->display->display_options['fields']['status']['field'] = 'status';
  $handler->display->display_options['fields']['status']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['status']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['status']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['status']['alter']['external'] = 0;
  $handler->display->display_options['fields']['status']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['status']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['status']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['status']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['status']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['status']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['status']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['status']['alter']['html'] = 0;
  $handler->display->display_options['fields']['status']['element_type'] = 'strong';
  $handler->display->display_options['fields']['status']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['status']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['status']['empty'] = 'N/A';
  $handler->display->display_options['fields']['status']['hide_empty'] = 0;
  $handler->display->display_options['fields']['status']['empty_zero'] = 0;
  $handler->display->display_options['fields']['status']['hide_alter_empty'] = 0;
  /* Field: Async Command: Message */
  $handler->display->display_options['fields']['message']['id'] = 'message';
  $handler->display->display_options['fields']['message']['table'] = 'async_command';
  $handler->display->display_options['fields']['message']['field'] = 'message';
  $handler->display->display_options['fields']['message']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['message']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['message']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['message']['alter']['external'] = 0;
  $handler->display->display_options['fields']['message']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['message']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['message']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['message']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['message']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['message']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['message']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['message']['alter']['html'] = 0;
  $handler->display->display_options['fields']['message']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['message']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['message']['hide_empty'] = 0;
  $handler->display->display_options['fields']['message']['empty_zero'] = 0;
  $handler->display->display_options['fields']['message']['hide_alter_empty'] = 0;
  /* Field: Async Command: Parameter number1 */
  $handler->display->display_options['fields']['number1']['id'] = 'number1';
  $handler->display->display_options['fields']['number1']['table'] = 'async_command';
  $handler->display->display_options['fields']['number1']['field'] = 'number1';
  $handler->display->display_options['fields']['number1']['label'] = 'Number of users';
  $handler->display->display_options['fields']['number1']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['external'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['number1']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['number1']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['number1']['alter']['html'] = 0;
  $handler->display->display_options['fields']['number1']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['number1']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['number1']['hide_empty'] = 1;
  $handler->display->display_options['fields']['number1']['empty_zero'] = 0;
  $handler->display->display_options['fields']['number1']['hide_alter_empty'] = 1;
  $handler->display->display_options['fields']['number1']['format_plural'] = 0;
  /* Field: Async Command: Parameter number2 */
  $handler->display->display_options['fields']['number2']['id'] = 'number2';
  $handler->display->display_options['fields']['number2']['table'] = 'async_command';
  $handler->display->display_options['fields']['number2']['field'] = 'number2';
  $handler->display->display_options['fields']['number2']['label'] = 'Number of items';
  $handler->display->display_options['fields']['number2']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['external'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['number2']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['number2']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['number2']['alter']['html'] = 0;
  $handler->display->display_options['fields']['number2']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['number2']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['number2']['hide_empty'] = 1;
  $handler->display->display_options['fields']['number2']['empty_zero'] = 0;
  $handler->display->display_options['fields']['number2']['hide_alter_empty'] = 1;
  $handler->display->display_options['fields']['number2']['format_plural'] = 0;
  /* Field: Async Command: Parameter number3 */
  $handler->display->display_options['fields']['number3']['id'] = 'number3';
  $handler->display->display_options['fields']['number3']['table'] = 'async_command';
  $handler->display->display_options['fields']['number3']['field'] = 'number3';
  $handler->display->display_options['fields']['number3']['label'] = 'Number of similarity records';
  $handler->display->display_options['fields']['number3']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['external'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['number3']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['number3']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['number3']['alter']['html'] = 0;
  $handler->display->display_options['fields']['number3']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['number3']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['number3']['hide_empty'] = 1;
  $handler->display->display_options['fields']['number3']['empty_zero'] = 0;
  $handler->display->display_options['fields']['number3']['hide_alter_empty'] = 1;
  $handler->display->display_options['fields']['number3']['format_plural'] = 0;
  /* Field: Async Command: Parameter number4 */
  $handler->display->display_options['fields']['number4']['id'] = 'number4';
  $handler->display->display_options['fields']['number4']['table'] = 'async_command';
  $handler->display->display_options['fields']['number4']['field'] = 'number4';
  $handler->display->display_options['fields']['number4']['label'] = 'Number of prediction records';
  $handler->display->display_options['fields']['number4']['alter']['alter_text'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['make_link'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['absolute'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['external'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['replace_spaces'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['trim_whitespace'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['nl2br'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['word_boundary'] = 1;
  $handler->display->display_options['fields']['number4']['alter']['ellipsis'] = 1;
  $handler->display->display_options['fields']['number4']['alter']['strip_tags'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['trim'] = 0;
  $handler->display->display_options['fields']['number4']['alter']['html'] = 0;
  $handler->display->display_options['fields']['number4']['element_label_colon'] = 1;
  $handler->display->display_options['fields']['number4']['element_default_classes'] = 1;
  $handler->display->display_options['fields']['number4']['hide_empty'] = 1;
  $handler->display->display_options['fields']['number4']['empty_zero'] = 0;
  $handler->display->display_options['fields']['number4']['hide_alter_empty'] = 1;
  $handler->display->display_options['fields']['number4']['format_plural'] = 0;
  /* Sort criterion: Async Command: Created */
  $handler->display->display_options['sorts']['created']['id'] = 'created';
  $handler->display->display_options['sorts']['created']['table'] = 'async_command';
  $handler->display->display_options['sorts']['created']['field'] = 'created';
  $handler->display->display_options['sorts']['created']['order'] = 'DESC';
  /* Sort criterion: Async Command: ID */
  $handler->display->display_options['sorts']['id']['id'] = 'id';
  $handler->display->display_options['sorts']['id']['table'] = 'async_command';
  $handler->display->display_options['sorts']['id']['field'] = 'id';
  $handler->display->display_options['sorts']['id']['order'] = 'DESC';
  /* Filter criterion: Async Command: Application */
  $handler->display->display_options['filters']['app']['id'] = 'app';
  $handler->display->display_options['filters']['app']['table'] = 'async_command';
  $handler->display->display_options['filters']['app']['field'] = 'app';
  $handler->display->display_options['filters']['app']['value'] = 'recommender';

  return $view;
}