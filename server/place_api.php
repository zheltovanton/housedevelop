<?php
if (isset($_GET['username']) && $_GET['username'] != '') {

$xml=file_get_contents('https://maps.googleapis.com/maps/api/place/autocomplete/json?input='.$_GET['input'].'&types=geocode&sensor=false&key=AIzaSyDnnbPg8EPLPWcwSYf8h2J6Cy_AkxXXiWI');

if (!$xml)
{
    trigger_error('Error reading XML file',E_USER_ERROR);
}
echo $xml;
}
?>
