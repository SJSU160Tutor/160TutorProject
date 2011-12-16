<?php
    echo "what!";
    
    require_once("facebook.php");
    
    echo "Welcome";
    
    $config = array();
    $config[‘appId’] = '284710104884292';
    $config[‘secret’] = '59d744f978686c9a66b378c7fc2c7dde';
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