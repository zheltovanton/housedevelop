<?php
// database settings 
function safe($value){ 
return addslashes(mysql_real_escape_string($value));
}

if (isset($_GET['tag']) && $_GET['tag'] != '') 
{
    $tag       = $_GET['tag'];
    error_log("tag $tag\n",3, "/usr/local/apache/logs/error_log");
//-----------------------------------------------------------------------------------------

    error_log($tag,0);  
    require_once 'include/db_functions.php';

    $db = new DB_Functions();

    if ($tag == 'loadhouseinfo') {
    
    // get marker position and split it for database
    $id       = $_GET['id'];
    //error_log("loadhouseinfo ".$id,0);
    
    $query="SELECT id , (SELECT name FROM users WHERE unique_id = user_uid) as username, name , lat , lng , 
	shothdesc , user_uid , developer , developer_desc , address , city , region , country , housedetails , created_at , updated_at, develop_finish_at, develop_begin_at, stage
	FROM house WHERE id = ".$id;
    $result = mysql_query($query) or die(mysql_error());
        // check for result 
    $no_of_rows = mysql_num_rows($result);
    error_log("loadhouseinfo rows ".$no_of_rows,0);
    if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
	    $address = $result["address"];
            $response["error"] = FALSE;

	    if ($result["city"]!='') {$address=$address.", ".$result["city"];}
	    if ($result["region"]!='') {$address=$address.", ".$result["region"];}
	    if ($result["country"]!='') {$address=$address.", ".$result["country"];}
            $response["address"] = $address;
            $response["address2"] = $result["address"];
            $response["city"] = $result["city"];
            $response["region"] = $result["region"];
            $response["country"] = $result["country"];

            $response["houseinfo"] = $result["housedetails"];

	    $developer = $result["developer"];
	    if (isset($result["developer_desc"])) {$developer=$developer.": ".$result["developer_desc"];}
            $response["developer"] = $developer;

            $response["developer2"] = $result["developer"];
            $response["developer_details"] = $result["developer_desc"];

            $response["user_uid"] = $result["user_uid"];
            $response["username"] = $result["username"];
	    $response["develop_finish_at"] = $result["develop_finish_at"];
	    $response["develop_begin_at"] = $result["develop_begin_at"];
	    $response["stage"] = $result["stage"];

            echo json_encode($response);
            // return user details
        } else {
          error_log("HTTP/1.1 500 Error: Could not load marker! $query",0);  
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not load marker";
            echo json_encode($response);
        }
}


//-----------------------------------------------------------------------------------------

    if ($tag == 'marker_edit') {
	//    error_log('marker_add',0);

	// Check User moderator OR owner of marker (house)
	
	error_log("edit check\n",3, "/usr/local/apache/logs/error_log");
	$query_checkmoder="SELECT users.name,users.moderator
			FROM users WHERE users.unique_id='".$_GET["uid"]."'";
	$query_checkowner="SELECT house.id, house.user_uid, users.name,users.moderator
			FROM house
			INNER JOIN users ON users.unique_id = house.user_uid
			WHERE house.id=".$_GET["id"];
        $resultsmoder = mysql_query($query_checkmoder);
        $resultsowner = mysql_query($query_checkowner);
        $no_of_rows1 = mysql_num_rows($resultsmoder);
	if ($no_of_rows1 > 0) {
            $resultmod = mysql_fetch_array($resultsmoder);
	}
//	mail('anton.zheltov@gmail.com', 'House edit', 'Edit house '.$_GET['id'].' user '.$user_uid.' ip '.$_SERVER['REMOTE_ADDR']);

	error_log(" edit check moder ".$resultmod["moderator"]."\n",3, "/usr/local/apache/logs/error_log");
	error_log($query_checkowner."  edit check owner ".mysql_num_rows($resultsowner)."\n",3, "/usr/local/apache/logs/error_log");

          if ((mysql_num_rows($resultsowner)>0) or ($resultmod["moderator"])) {
		// Save data if permisson OK
		error_log("edit save \n",3, "/usr/local/apache/logs/error_log");
		//more validations are encouraged, empty fields etc.
                $user_uid   = safe(filter_var($_GET["creator_uid"], FILTER_SANITIZE_STRING));
	        $mAddress   = safe(filter_var($_GET["address"], FILTER_SANITIZE_STRING));
                $city   = safe(filter_var($_GET["city"], FILTER_SANITIZE_STRING));
   	        $country   = safe(filter_var($_GET["country"], FILTER_SANITIZE_STRING));
                $region   = safe(filter_var($_GET["region"], FILTER_SANITIZE_STRING));
                $developer   = safe(filter_var($_GET["developer"], FILTER_SANITIZE_STRING));
                $developer_desc  = safe(filter_var($_GET["developer_details"], FILTER_SANITIZE_STRING));
                $housedetails   = safe(filter_var($_GET["housedetails"], FILTER_SANITIZE_STRING));
                $develop_finish_at   = safe(filter_var($_GET["develop_finish_at"], FILTER_SANITIZE_STRING));
                $develop_begin_at   = safe(filter_var($_GET["develop_begin_at"], FILTER_SANITIZE_STRING));
                $stage   = safe(filter_var($_GET["stage"], FILTER_SANITIZE_STRING));
		//UPDATE `housedevelop`.`house` SET `lng` = '41.437921',`stage` = 'zero' WHERE `house`.`id` =11;    
    		$query="UPDATE house set 
			address='$mAddress',
			city='$city', 
			country='$country', 
			region='$region', 
			developer='$developer', 
			developer_desc='$developer_desc', 
	    		housedetails='$housedetails',  
			develop_finish_at='$develop_finish_at', 
			develop_begin_at='$develop_begin_at', 
			updated_at=NOW(),
			stage='$stage'
			WHERE house.id=".$_GET["id"] ;
		error_log("edit query ".$query." \n",3, "/usr/local/apache/logs/error_log");
                mail('anton.zheltov@gmail.com', 'House edit', 'Edit house' );
//	        mail('anton.zheltov@gmail.com', 'House edit', 'Edit house '.$_GET['id'].' user '.$user_uid.' ip '.$_SERVER['REMOTE_ADDR']);
		$results = mysql_query($query);
		error_log(mysql_errno() . ": " . mysql_error()." \n",3, "/usr/local/apache/logs/error_log");
	        if ($results) {
        	    // get user details 
        	    $response["error"] = FALSE;

	    	    echo json_encode($response);
 
            // return user details
	        } else {
        	  error_log("HTTP/1.1 500 Error: Could not save marker! $query",0);  
	            $response["error"] = TRUE;
        	    $response["error_msg"] = "Error: Could not save marker";
	            echo json_encode($response);
        	}
	}
}


}

if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];
    error_log("tag $tag\n",3, "/usr/local/apache/logs/error_log");
    // include db handler
    require_once 'include/db_functions.php';
    $db = new DB_Functions();
    // response Array
    $response = array("tag" => $tag, "error" => FALSE);

    // check for tag type

    if ($tag == 'marker_add') {
//    error_log('marker_add',0);
    
    // get marker position and split it for database
    $mLat       = filter_var($_POST['lat'], FILTER_VALIDATE_FLOAT);
    $mLng       = filter_var($_POST['lng'], FILTER_VALIDATE_FLOAT);
    
        //more validations are encouraged, empty fields etc.
    $mAddress   = filter_var($_POST["address"], FILTER_SANITIZE_STRING);
    $user_uid   = filter_var($_POST["creator_uid"], FILTER_SANITIZE_STRING);
    $city   = filter_var($_POST["city"], FILTER_SANITIZE_STRING);
    $country   = filter_var($_POST["country"], FILTER_SANITIZE_STRING);
    $region   = filter_var($_POST["region"], FILTER_SANITIZE_STRING);
    $developer   = filter_var($_POST["developer"], FILTER_SANITIZE_STRING);
    $developer_desc  = filter_var($_POST["developer_details"], FILTER_SANITIZE_STRING);
    $housedetails   = filter_var($_POST["housedetails"], FILTER_SANITIZE_STRING);
    $develop_finish_at   = filter_var($_POST["develop_finish_at"], FILTER_SANITIZE_STRING);
    $develop_begin_at   = filter_var($_POST["develop_begin_at"], FILTER_SANITIZE_STRING);
    $stage   = filter_var($_POST["stage"], FILTER_SANITIZE_STRING);
    
    $query="INSERT INTO house (user_uid,lat, lng, address, city, country, region, developer, developer_desc, 
	    		       housedetails,  develop_finish_at, develop_begin_at, stage, created_at) 
            VALUES ('$user_uid', $mLat, $mLng, '$mAddress', '$city', '$country', '$region', '$developer', 
		    '$developer_desc', '$housedetails', '$develop_finish_at', '$develop_begin_at', '$stage', NOW())";
    $results = mysql_query($query);
        if ($results) {
            // get user details 
            $uid = mysql_insert_id(); // last inserted id
            $result =  $uid;
            $response["error"] = FALSE;
            $response["uid"] = $user["unique_id"];
	    echo json_encode($response);
 
            // return user details
        } else {
          error_log("HTTP/1.1 500 Error: Could not create marker! $query",0);  
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not create marker";
            echo json_encode($response);
        }
}

//-----------------------------------------------------------------------------------------



//-----------------------------------------------------------------------------------------

if ($tag == 'markers_get') {
	$lat = $_POST['lat'];
	$lon = $_POST['lon'];
	$dist = $_POST['dist'];

	error_log($lat." ".$lon." ".$dist,0);
	$search_sql = "SELECT id, address,lat, lng,developer, 
	( 3959 * acos( cos( radians(".$lat.") ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(".$lon.") ) + sin( radians(".$lat.") ) * sin( radians( lat ) ) ) ) AS distance
	FROM house HAVING distance < ".$dist." ORDER BY distance";
	//error_log($search_sql,0);
	$results = mysql_query($search_sql);
        $no_of_rows = mysql_num_rows($results);
	error_log("Select markers, return ".$no_of_rows,0);

        if ($no_of_rows > 0) {
	while ($row = mysql_fetch_array($results, MYSQL_NUM)) {	
		//error_log($row['0'].$row['1'].$row['2'].$row['3'],0);
		$return_data[] = array(
      		'address' => $row['1'],
      		'lat' => $row['2'],
      		'lng' => $row['3'],
      		'id'   => $row['0'],
		'developer'   => $row['4'],
		'distance' => $row['5']);
	}}
        else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get markers";
	    error_log("Error: Could not get markers",0);
            echo json_encode($response);
        }//end if
        }else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get markers";
            echo json_encode($response);
    }
    $bd_json = json_encode($return_data);
    echo $bd_json;
} 


?>

