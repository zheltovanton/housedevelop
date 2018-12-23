<?php
// database settings
//    error_log($_GET['tag']."\n",3, "/usr/local/apache/logs/error_log");
 
if (isset($_GET['tag']) && $_GET['tag'] != '') 
{
    $tag       = $_GET['tag'];
//-----------------------------------------------------------------------------------------

    error_log($tag,0);  
    require_once 'include/db_functions.php';

    $db = new DB_Functions();

if ($tag == 'is_moderator') {
    
    // get marker position and split it for database
    $userid = $_GET['user_id'];
//    error_log($userid."\n",3, "/usr/local/apache/logs/error_log");

    //error_log("loadhouseinfo ".$id,0);
    
$query="SELECT * FROM users where unique_id =  '".$userid."';
    //error_log($query."\n",3, "/usr/local/apache/logs/error_log");

    $result = mysql_query($query) or die(mysql_error());
        // check for result 
    $no_of_rows = mysql_num_rows($result);
    error_log("fav rows ".$no_of_rows,3, "/usr/local/apache/logs/error_log");
        if ($no_of_rows > 0) {
	while ($row = mysql_fetch_array($result, MYSQL_NUM)) {	
		//error_log($row['0'].$row['1'].$row['2'].$row['3'],0);
		$return_data['moderator'] = $row[6];
	}
    	$bd_json = json_encode($return_data);
    	echo $bd_json;
	error_log("moder json ".$bd_json,3, "/usr/local/apache/logs/error_log");

	}
        else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get right";
	    error_log("Error: Could not get favs",0);
            echo json_encode($response);
        }//end if
        }else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get right";
            echo json_encode($response);
    	}
}

?>

