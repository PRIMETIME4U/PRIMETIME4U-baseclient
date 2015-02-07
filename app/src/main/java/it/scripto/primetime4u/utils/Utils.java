package it.scripto.primetime4u.utils;

public class Utils {
    
    public static final String SERVER_URL = "http://hale-kite-786.appspot.com/";
    public static final String SERVER_API = SERVER_URL + "api/";
    
    public static String sanitize(String string, boolean imdb) {
        if (imdb) {
            return string.replace(" ", "_");
        } else {
            return string.replace(" ", "%20");
        }
    }
}
