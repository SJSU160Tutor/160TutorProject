<?php
    require_once("customDBSettings.php");    
    
    $added = FALSE;
    if (isset($_POST["tutorSessionId"]) && isset($_POST["tutorId"]) && isset($_POST["ratedById"]) && isset($_POST["ratedByFbId"]) && isset($_POST["rating"]))
    {
        mysql_connect('localhost',$cdb_username,$cdb_password);
        mysql_select_db($cdb_database);
        
        $alreadyRated = FALSE;
        $ratingRequest = "SELECT `uId` FROM `tutor_ratings` WHERE `tutorSessionId` = ".mysql_real_escape_string($_POST["tutorSessionId"])." AND `ratedByUser` = ".mysql_real_escape_string($_POST["ratedById"]);
        $ratingInfo = mysql_query($ratingRequest);
        if ($ratingInfo && mysql_numrows($ratingInfo) > 0)
        {$alreadyRated = TRUE;}
        
        if (!$alreadyRated)
        {
            $addRatingRequest = "INSERT INTO `tutor_ratings` VALUES ('', ".
            mysql_real_escape_string($_POST["tutorId"]).", ".
            mysql_real_escape_string($_POST["tutorSessionId"]).", ".
            mysql_real_escape_string($_POST["rating"]).", ".
            mysql_real_escape_string($_POST["ratedById"]).", ".
            mysql_real_escape_string($_POST["ratedByFbId"]).")";
            mysql_query($addRatingRequest);
        }
        
        if (!$alreadyRated && mysql_errno() == 0)
        {
            $added = TRUE;
        }
        
        mysql_close();
    }
    
    if ($added)
    {
        header("HTTP/1.1 200 OK");
        echo "Thanks for rating this tutor!";
    }
    else
    {
        header("HTTP/1.1 200 OK");
        echo "Sorry, there was an error, please try again later.";
    }
    
    ?>