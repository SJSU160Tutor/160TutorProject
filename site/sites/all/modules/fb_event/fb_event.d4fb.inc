<?php


/**
 * Implementation of hook_node_view().
 */
function fb_event_d4fb_node_view($node, $arg) {

	global $user;
	
	if ($label = variable_get(FB_CONNECT_VAR_PRIMARY, NULL)) {
		$primary_app = fb_get_app(array('label' => $label));
	}
	
	$fbuid = 0;
	$eid = '';
	$status = '';
	$has_granted_perm = FALSE;
	$is_app_user = FALSE;
	$total_rsvp = 0;
	
	// Check that we can create a fbconnect api client object and
	// have an fbeid field on this node
	if (!empty($node->field_fbeid) && $fb = fb_api_init($primary_app)) {
		$eid = $node->field_fbeid['und'][0]['value'];
	
		if($fbuid = fb_get_fbu($user->uid)){
			
			try {
				// has this user authorised this app before?
				$is_app_user = $fb->api(array(
				'method' => 'users.isAppUser',
				'uid' => $fbuid));
			}
			catch (Exception $e) {
				watchdog('fb_event', 'Exception thrown while using users_isAppUser: @code, Eid: @eid', array('@code' => $e->getMessage(), '@eid' => $eid), WATCHDOG_WARNING);
				$is_app_user = 0;
			}
		}
	}
	
	// by now we know the status of the user vis-a-vis fb
	$fbc_status = ($user->uid && isset($fbuid) && $fbuid && $is_app_user) 
		? FB_EVENT_FULL_CONNECT 
		: (isset($fbuid) && $fbuid && $is_app_user 
			? FB_EVENT_NO_UID 
			: (isset($fbuid) && $fbuid 
				? FB_EVENT_NO_FBC 
				: $user->uid 
					? FB_EVENT_USER_NO_FB 
					: FB_EVENT_NO_LOGIN));

	$attending = array();
	$event_status = $perm_status = NULL;
	$event_form = array();
	
	// TODO: add handling for passing a numeric eid that does 
	// not correspond to an existing fb event
	if ( !empty($eid) ) {
		$num_members_to_show = variable_get('fb_event_show_members', 10);
		
		if ($num_members_to_show) {
			$members = fb_event_event_get_members($fb, $eid);
	
			if ($members) {
				$total_rsvp = count($members['data']);
				shuffle($members['data']);
				$attending = array_slice($members['data'], 0, $num_members_to_show);
			}
		}
		
		if ($fbc_status == FB_EVENT_FULL_CONNECT) {
			$my_status = NULL;
			
			$has_granted_perm = FALSE;
			
			try {
				// check whether the user has already authorised the app to RSVP
				// to FB events
				$has_granted_perm = $fb->api(array(
					'method' => 'users.hasAppPermission', 
					'uid' => $fbuid,
					'ext_perm' => 'rsvp_event'
				));
			}
			catch (Exception $e) {
				watchdog('fb_event', 'Exception thrown while using users_hasAppPermission: @code, Eid: @eid', array('@code' => $e->getMessage(), '@eid' => $eid), WATCHDOG_WARNING);
			}
			
			if ($has_granted_perm) {
				$fql = "SELECT rsvp_status 
				FROM event_member 
				WHERE uid = $fbuid
				AND eid = '$eid'";
				
				$my_status = $fb->api(array(
				'method' => 'fql.query',
				'query' => $fql));
				
				//fb is currently returning 'unsure' from fql query, but wants 'maybe' for graph api status change
				if ( isset($my_status[0]['rsvp_status']) ) {
					if($my_status[0]['rsvp_status'] == 'unsure'){
						$status = 'maybe';
					}else{
						$status = $my_status[0]['rsvp_status'];
					}
				}
				
				$event_form = drupal_render(drupal_get_form('fb_event_signup_form', $eid, $fbuid, $status));
				
			}
		}
		
		$signup_text = variable_get('fb_event_signup_text');
		
		// send everything to our tpl file
		$output = theme('fb_event_main', array(
			'eid' => $eid, 
			'fbuid' => $fbuid, 
			'fbc_status' => $fbc_status, 
			'members'	=> $attending,
			'my_status' => $status, 
			'perm_status'	=> $has_granted_perm, 
			'event_status' => $event_status,
			'event_form' => $event_form, 
			'signup_text' => $signup_text, 
			'total_rsvp' => $total_rsvp
			'fb_login_button' => theme('fb_login_button', 'Login with Facebook'),
			)
		);
		
		// add the themed output to the node content array
		$node->content['fb_event'] = array(
			'#markup' => $output,
			'#weight' => 0,
		);
	}
}


function  fb_event_d4fb_signup_form_submit($form, &$form_state) {
    if ($label = variable_get(FB_CONNECT_VAR_PRIMARY, NULL)) {
		$primary_app = fb_get_app(array('label' => $label));
	}

	if ($fb = fb_api_init($primary_app)) {
		try {
			// send the RSVP
			$status = $fb->api('/'.$form_state['values']['event_id'].'/'.
			$form_state['values']['event_status'], 'POST', array());
			if ($status) {
				drupal_set_message(t('RSVP sent!'), 'status');
			}
		}
		catch (Exception $e) {
			watchdog('fb_event', 'Exception thrown while using fb_event signup form : @code Eid: @eid', array('@code' => $e->getMessage(), '@eid' => $form_state['values']['event_id']), WATCHDOG_WARNING);
		}
	}
}



function fb_event_d4fb_event_get_members($fb, $eid) {
	
	if (!$fb) {
		return array();
	}
	
	try {
		$members = $fb->api('/'.$eid.'/attending', array('access_token' => fb_get_token($fb)));
	}
	catch (Exception $e) {
		watchdog('fb_event', 'Exception thrown while using fbconnect_event_get_members: @code, Eid: @eid', array('@code' => $e->getMessage(), '@eid' => $eid), WATCHDOG_WARNING);
	}
	
	return $members;
}
