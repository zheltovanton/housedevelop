<?php

function createThumb( $pathToImages, $pathToThumbs) 
{
      $img = imagecreatefromjpeg( $pathToImages );
      $width = imagesx( $img );
      $height = imagesy( $img );
      $new_width = imagesx( $img );
      $new_height = imagesy( $img );


      // calculate thumbnail size
      if ($width>$height) {
	      if ($width>400) {$new_width = 400;}
	      $new_height = floor( $height * ( $new_width / $width ) );
	} else
	{
	   if ($height>400) {$new_height = 400;}
	   $new_width = floor( $width * ( $new_height / $height ) );
	}

      // create a new temporary image
      error_log("--11--".$new_width." ".$new_height);
      $tmp_img = imagecreatetruecolor( $new_width, $new_height );

      // copy and resize old image into new image 
      imagecopyresized( $tmp_img, $img, 0, 0, 0, 0, $new_width, $new_height, $width, $height );

      // save thumbnail into a file
      imagejpeg( $tmp_img, $pathToThumbs );

}

function resize( $pathToImages, $pathToThumbs) 
{
      $img1 = imagecreatefromjpeg( $pathToImages );
      $width = imagesx( $img1 );
      $height = imagesy( $img1 );
      $new_width = imagesx( $img1 );
      $new_height = imagesy( $img1 );

      // calculate thumbnail size
      if ($width>$height) {
	      if ($width>1200) {$new_width = 1200;}
	      $new_height = floor( $height * ( $new_width / $width ) );
	} else
	{
	   if ($height>1200) {$new_height = 1200;}
	   $new_width = floor( $width * ( $new_height / $height ) );
	}

      // create a new temporary image
      //error_log("--22--".$new_width." ".$new_height);
      $tmp_img1 = imagecreatetruecolor( $new_width, $new_height );

      // copy and resize old image into new image 
      imagecopyresized( $tmp_img1, $img1, 0, 0, 0, 0, $new_width, $new_height, $width, $height );

      // save thumbnail into a file
      imagejpeg( $tmp_img1, $pathToThumbs );
}

function parseUtf8ToIso88591($string){
     if(!is_null($string)){
            $iso88591_1 = utf8_decode($string);
            $iso88591_2 = iconv('UTF-8', 'ISO-8859-1', $string);
            $string = mb_convert_encoding($string, 'ISO-8859-1', 'UTF-8');  
            return "";
     }
     return $string;
}

function generateRandomString($length = 10) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

$headers = apache_request_headers();

foreach ($headers as $header => $value) {
    if ($header=="Tag") $tag='upload_photo';

    if ($header=="Marker") $marker_id=$value;

    if ($header=="Userid") $user_id=$value;
    if ($header=="Comment") $comment=$value;
 //   error_log("post ".$header.":".$value."\n",3, "/usr/local/apache/logs/error_log");

}

error_log("add image name ".$_FILES['uploaded_file']['name']."\n",3, "/usr/local/apache/logs/error_log");

if ($tag='upload_photo')
{
$target_path_upload = "/usr/local/apache/htdocs/uploads/";
$target_path_thumb = "/usr/local/apache/htdocs/thumbs/";
$target_path_tmp = "/usr/local/apache/htdocs/tmp/";
$prefix=generateRandomString();
require_once 'include/db_functions.php';
$db = new DB_Functions();

$comment = utf8_decode($comment);
//$user_id = utf8_decode($user_id);

$filename=$prefix.basename($_FILES['uploaded_file']['name']);
$target_path1 = $target_path_tmp.$filename;
if ($_FILES['uploaded_file']['name']=="none") {
	$query="INSERT INTO comments(filename, user_id, marker_id,comment) VALUES('none', '$user_id', '$marker_id', '$comment')";
	//access_log($query);
	//echo $query;
	   $result = mysql_query($query);
	   // check for successful store
	   if ($result) {
	       // get user details 
	       error_log ("success",0);
		return true;
	   } else {
	       return false;
	   }

} else {
if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $target_path1)) 
{
	if (exif_imagetype($target_path1)== IMAGETYPE_JPEG) {
		resize( $target_path1,$target_path_upload.$filename) ;
		createThumb($target_path_upload.$filename,$target_path_thumb.$filename); 
	}else{
		copy($target_path1,$target_path_thumb.$filename);
                copy($target_path1,$target_path_upload.$filename);

	}
	unlink($target_path1);
	//error_log("try save to db ".$user_id." ".$marker_id,0);
	$query="INSERT INTO comments(filename, user_id, marker_id,comment) VALUES('$filename', '$user_id', '$marker_id', '$comment')";
	//access_log($query);
	//echo $query;
	   $result = mysql_query($query);
	   // check for successful store
	   if ($result) {
	       // get user details 
	       error_log ("success",0);
		return true;
	   } else {
	       return false;
	   }
    
}}
}
?>
