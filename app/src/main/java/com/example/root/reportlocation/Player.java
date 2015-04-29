package com.example.root.reportlocation;

/**
 * Created by thorin_oakenshield on 4/22/15.
 */
public final class Player {

    static double latitude;
    static double longitude;
    static int id = 21;
    static String name = "dportology";
    static String password = "9872598725";


    public static  void setLatitude(double lat){
        latitude = lat;
    }
    public static  double getLatitude(){
        return latitude;
    }
    public static  void setLongitude(double lon){
        longitude = lon;
    }
    public static  double getLongitude(){
        return longitude;
    }
    public static  int getId(){
        return id;
    }
    public static  String getName(){
        return name;
    }
}
