<?php
    echo "what!";
    
    require_once("sites/all/libraries/facebook-php-sdk/src/facebook.php") or die("Killed: No facebook include file found.");
    
    echo "Welcome";
    
    $config = array();
    $config[‘appId’] = 'YOUR_APP_ID';
    $config[‘secret’] = 'YOUR_APP_SECRET';
    $config[‘fileUpload’] = false; // optional
    
    $facebook = new Facebook($config);
    
    echo "The currently logged in user is: ", $facebook->getUser();
    
    
    //user's friend's id's in array form
    function friendList()
    {
        
    }
    
    //unweighted rating for tutor with uId
    
    //weighted average for tutor by logged in user
    function weightedRating($rateCategoryDbColumn, $tutorId, $userFriendList)
    {
        
    }
    
    
?>