<?php


if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];
    // include db handler
    require_once 'include/db_functions.php';
    $db = new DB_Functions();
    // response Array
    $response = array("tag" => $tag, "error" => FALSE);

    // check for tag type
//-----------------------------------------------------------------------------------------

if ($tag == 'total_comments') {
//    error_log('marker_add',0);
    
    // get marker position and split it for database
    $mLat       = filter_var($_POST['id'], FILTER_VALIDATE_FLOAT);
    
    $query="select count(*) as cnt from comments where house_id=".$id;  
    $results = mysql_query($query);
        if ($results) {
            // get user details 
            $uid = mysql_insert_id(); // last inserted id
            $result =  $uid;
            $response["error"] = FALSE;
            $response["cnt"] = $results["cnt"];
	    echo json_encode($response);
 
            // return user details
        } else {
          error_log("HTTP/1.1 500 Error: Could select count! $query",0);  
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could select count";
            echo json_encode($response);
        }
}
}
//echo ($_GET['tag']);

if (isset($_GET['tag'])) {
//-----------------------------------------------------------------------------------------

if (($_GET['tag'] == 'comments_get')&&(isset($_GET['uid']))) {
    require_once 'include/db_functions.php';
    $db = new DB_Functions();
	$marker_id = $_GET['marker_id'];
	$uid = $_GET['uid'];
	$search_sql = "SELECT filename, comment, (SELECT name FROM users WHERE unique_id = user_id) as username, 
		       date from comments where marker_id=".$marker_id." order by date desc LIMIT 0 , 30";
	$results = mysql_query($search_sql);
        $no_of_rows = mysql_num_rows($results);
	error_log("Select comments, return ".$no_of_rows,0);
	//echo $search_sql;
        if ($no_of_rows > 0) 
	{
	while ($row = mysql_fetch_array($results, MYSQL_NUM)) 
	  {	
		//error_log($row['0'].$row['1'].$row['2'].$row['3'],0);
		$return_data[] = array(
      		'filename' => $row['0'],
      		'comment' => $row['1'],
      		'username' => $row['2'],
      		'date'   => $row['3']);
	  }
	} else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get markers";
	    error_log("Error: Could not get markers",0);
            echo json_encode($response);
        }//end if
    $bd_json = json_encode($return_data);
    echo $bd_json;
}
} 
?>
