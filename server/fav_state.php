<?php
// database settings
    error_log($_GET['tag']."\n",3, "/usr/local/apache/logs/error_log");
 
if (isset($_GET['tag']) && $_GET['tag'] != '') 
{
    $tag       = $_GET['tag'];
//-----------------------------------------------------------------------------------------

    error_log($tag,0);  
    require_once 'include/db_functions.php';

    $db = new DB_Functions();

if ($tag == 'fav_get') {
    
    // get marker position and split it for database
    $id       = $_GET['marker_id'];
    $userid = $_GET['uid'];
 //   error_log($id." ".$userid."\n",3, "/usr/local/apache/logs/error_log");

    //error_log("loadhouseinfo ".$id,0);
    
    $query="SELECT fav_id from fav WHERE (fav_user_id = '".$userid."') and (fav_house_id=".$id.")";
    $result = mysql_query($query) or die(mysql_error());
        // check for result 
    $no_of_rows = mysql_num_rows($result);
    //error_log("fav rows ".$no_of_rows,3, "/usr/local/apache/logs/error_log");
    if ($no_of_rows > 0) {
            //$result = mysql_fetch_array($result);
	    $result = "true";
            $response["state"] = $result;
            echo json_encode($response);
            // return user details
        } else {
          error_log("HTTP/1.1 500 Error: Could not load fav! $query",0);  
            $response["state"] = "false";
            echo json_encode($response);
        }
}

if ($tag == 'fav_set') {
    
    // get marker position and split it for database
    $id       = $_GET['marker_id'];
    $userid = $_GET['uid'];
    error_log($id." ".$userid."\n",3, "/usr/local/apache/logs/error_log");

    $query="insert into fav (fav_user_id, fav_house_id) values ( '".$userid."',".$id.")";
    $result = mysql_query($query) or die(mysql_error());
        // check for result 
    if ($results) {
            //$result = mysql_fetch_array($result);
	    $result = "true";
            $response["state"] = $result;
            echo json_encode($response);
            // return user details
     } else {
          error_log("HTTP/1.1 500 Error: Could not set fav! $query",0);  
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not set fav";
            echo json_encode($response);
        }
}

if ($tag == 'fav_delete') {
    
    // get marker position and split it for database
    $id       = $_GET['marker_id'];
    $userid = $_GET['uid'];
    //error_log($id." ".$userid."\n",3, "/usr/local/apache/logs/error_log");

    $query="delete from fav WHERE (fav_user_id = '".$userid."') and (fav_house_id=".$id.")";
    //error_log($query."\n",3, "/usr/local/apache/logs/error_log");
    $result = mysql_query($query) or die(mysql_error());
        // check for result 
    if ($results) {
            //$result = mysql_fetch_array($result);
	    $result = "true";
            $response["state"] = $result;
            echo json_encode($response);
            // return user details
     } else {
          error_log("HTTP/1.1 500 Error: Could not delete fav! $query",0);  
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not delete fav";
            echo json_encode($response);
        }
}

}


?>

