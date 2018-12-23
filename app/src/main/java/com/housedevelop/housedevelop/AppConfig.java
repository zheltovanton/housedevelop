package com.housedevelop.housedevelop;
 
public class AppConfig {
    // Server user login url
    static String site = "http://housedevelop.com/";
    public static String URL_LOGIN = site + "index_api.php";
 
    // Server user register url
    public static String URL_REGISTER = site + "index_api.php";
    public static String URL_MARKERS = site + "markers.php";
    public static String URL_COMMENTS = site + "comments_list.php";
    public static String URL_FAV = site + "fav_state.php";
    public static String URL_FAVGET = site + "fav_get.php";
    public static String URL_UPLOAD_PHOTO = site + "upload_photo.php";
    public static String URL_MODERATOR = site + "user_right_get.php";

    //----------------------------------------------------------------------------------------------
    public static String myFormat(String str)
    {
        String str2 = str.replace("&#34;", "\"");
        return str2;
    }
}