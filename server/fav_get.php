<?php
 
if (isset($_GET['tag']) && $_GET['tag'] != '') 
{
    $tag       = $_GET['tag'];
//-----------------------------------------------------------------------------------------

    require_once 'include/db_functions.php';

    $db = new DB_Functions();

if ($tag == 'fav_getlist') {
    
    // get marker position and split it for database
    $userid = $_GET['user_id'];
//    error_log($userid."\n",3, "/usr/local/apache/logs/error_log");

    //error_log("loadhouseinfo ".$id,0);
    
     $query="SELECT * , (SELECT name FROM users WHERE users.unique_id = fav.fav_user_id ) AS username,
	(SELECT COMMENT FROM comments WHERE comments.marker_id = fav.fav_house_id ORDER BY id DESC LIMIT 1) AS lastcomment
	FROM fav INNER JOIN house ON house.id = fav.fav_house_id
	WHERE fav.fav_user_id =  '".$userid."'
	ORDER BY  'fav_date' DESC";
    //error_log($query."\n",3, "/usr/local/apache/logs/error_log");

    $result = mysql_query($query) or die(mysql_error());
        // check for result 
    $no_of_rows = mysql_num_rows($result);
    //error_log("fav rows ".$no_of_rows,3, "/usr/local/apache/logs/error_log");
        if ($no_of_rows > 0) {
	while ($row = mysql_fetch_array($result, MYSQL_NUM)) {	
		//error_log($row['0'].$row['1'].$row['2'].$row['3'],0);
		$return_data[] = array(
      		'fav_house_id' => $row[2],
      		'shothdesc' => $row[8],
      		'user_uid' => $row[9],
      		'developer'   => $row[10],
      		'developer_desc'   => $row[11],
      		'address'   => $row[12],
      		'city'   => $row[13],
      		'region'   => $row[14],
      		'country'   => $row[15],
      		'housedetails'   => $row[16],
		'created_at'   => $row[17],
		'username' => $row[19],
		'lastcomment' => $row[20]);
	}
    	$bd_json = json_encode($return_data);
    	echo $bd_json;
	//error_log("fav json ".$bd_json,3, "/usr/local/apache/logs/error_log");

	}
        else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get favs";
	    error_log("Error: Could not get favs",0);
            echo json_encode($response);
        }//end if
        }else{
            $response["error"] = TRUE;
            $response["error_msg"] = "Error: Could not get favs";
            echo json_encode($response);
    	}
}

?>

