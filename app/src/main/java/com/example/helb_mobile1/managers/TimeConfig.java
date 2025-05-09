package com.example.helb_mobile1.managers;


public class TimeConfig {
    /*
    config class as to make it easier to set different windows of time while debugging
    Note that this doesn't change when Firebase functions trigger, those have to be changed directly
    in the Firebase Console
     */

    public static final int PUBLISH_TIME_HOUR = 18; //supposed to be 18
    public static final int NEW_WORD_TIME_HOUR = 8; //supposed to be 8
    public static final String SERVER_TIMEZONE = "Europe/Brussels"; //supposed to be "Europe/Brussels"
}
