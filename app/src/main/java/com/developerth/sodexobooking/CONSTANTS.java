package com.developerth.sodexobooking;

/**
 * Created by MacBookPro on 5/19/2017 AD.
 */

public class CONSTANTS {

    final public static String Authorization="Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwic3ViIjoiYWRtaW5fZGV2IiwidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoicEBzc3cwcmQiLCJpYXQiOjE2MDI1OTM0NDd9.xBnTWDDZrBgiknC896KCAzUZOpiQdVvLpuFjgVqXa0w";

    //----1.
    final public static boolean IS_USE_TEST_URL =false;
    final public static boolean IS_EMULATION =false;
    final public static boolean IS_FAKE_IMEI =false;
    final public static boolean IS_FAKE_LAT_LNG =false;
    // final public static String PROTOCOL="http://";
    final public static String PROTOCOL="https://";

    //----2.
    final public static String SERVER_IP_ADDRESS = "https://node-js-sodexo.herokuapp.com/";

    final public static String URL_REGISTER                     = SERVER_IP_ADDRESS+"api/register";
    final public static String URL_LOGIN                        = "api/users/login";

}
