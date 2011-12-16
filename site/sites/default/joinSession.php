<?php
    require_once("customDBSettings.php");    
    
    $added = FALSE;
    if (isset($_POST["join"]) && isset($_POST["user"]))
    {
        mysql_connect('localhost',$cdb_username,$cdb_password);
        mysql_select_db($cdb_database);
        $tsaRequest = "SELECT `attendees` FROM `tutorSession_attendees` WHERE `tutorSessionId` = ".mysql_real_escape_string((int)$_POST["join"]);
        $tsaData = mysql_query($tsaRequest);
        
        $addUserSql = "";
        //add user to session if no results and no error
        if ($tsaData && mysql_numrows($tsaData) > 0)
        {   //at least one row
            if (strlen(mysql_result($tsaData,0,"attendees")) < 1)
            {
                $addUserSql = "UPDATE `tutorSession_attendees` SET `attendees` = ".mysql_real_escape_string($_POST["user"])." WHERE `tutorSessionId` = ".mysql_real_escape_string($_POST["join"])." LIMIT 1"; 
            }
        }
        else if ((!$tsaData || mysql_numrows($tsaData) < 1) && mysql_errno() == 0)
        {   //no row and no error, insert new row
            $addUserSql = "INSERT INTO `tutorSession_attendees` VALUES (".
            mysql_real_escape_string($_POST["join"]).", ".
            mysql_real_escape_string($_POST["user"]).", 0)";
        }
        
        if (strlen($addUserSql) > 0)
        {
            mysql_query($addUserSql);
            if (mysql_errno() == 0)
            {$added = TRUE;}
        }
            
        mysql_close();
        
        
    }
    
    if ($added)
    {
        header("HTTP/1.1 200 OK");
        echo "Thanks for joining this session.";
    }
    else
    {
        header("HTTP/1.1 200 OK");
        echo "Sorry, session full.";
    }
    
    ?>