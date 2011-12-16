@ echo off
@ rem If you install the module under 'sites\all\modules\recommeder' and 'sites\default\settings.php' is accessible, then you can run this script directly.
@ rem Otherwise, please see README and make necessary configuration to make this script running correctly.

SET DRUPAL_HOME=..\..\..\..
SET ASYNC_COMMAND_HOME=%DRUPAL_HOME%\sites\all\modules\async_command
SET RECOMMENDER_HOME=%DRUPAL_HOME%\sites\all\modules\recommender
SET MAHOUT_HOME=%DRUPAL_HOME%\sites\all\libraries\mahout

@ rem Test if the required programs (java and php) exist or not.
java -version 2>NULL || (echo Please install Java or put java.exe under PATH & GOTO :END)
php -v 2>NUL || (echo Please install PHP or put php.exe under PATH & GOTO :END)

@ rem Set classpath for the java program.
set CLASSPATH=%RECOMMENDER_HOME%\recommender.jar;$ASYNC_COMMAND_HOME\drupal-app.jar;%ASYNC_COMMAND_HOME%\lib\*;%MAHOUT_HOME%\*

@ rem Run recommender
java -cp %CLASSPATH% org.drupal.project.recommender.RecommenderApp

@ rem Set the path of config.properties if you would use it.
@ rem SET CONFIG_FILE=c:\config.properties
@ rem java -cp %CLASSPATH% org.drupal.project.recommender.RecommenderApp -c %CONFIG_FILE%

:END
