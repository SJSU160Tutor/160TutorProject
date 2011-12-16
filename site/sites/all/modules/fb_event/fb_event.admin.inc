<?php


/**
* Configuration settings form
*/
function fb_event_admin_configure() {
	$form = array();
	
	$form['fb_event_show_members'] = array(
		'#title' => t('Number of attendees'),
		'#description' => t('How many event attendees to show with event info - set to 0 to not show attendees at all'),
		'#type' => 'textfield',
		'#size' => 6,
		'#default_value' => variable_get('fb_event_show_members', 10)
	);
	$form['fb_event_signup_text'] = array(
		'#title' => t('Sign-up Authorization Text'),
		'#description' => t('Text to show above the fb button to grant extended permission for RSVPs'),
		'#type' => 'textfield',
		'#default_value' => variable_get('fb_event_signup_text', t('Allow @site to sign me up for events', array('@site' => variable_get('site_name', ''))))
	);
	return system_settings_form($form);
}
