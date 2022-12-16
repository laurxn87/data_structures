package uk.ac.warwick.cs126.util;
import java.lang.Math;

public class HaversineDistanceCalculator {

    private final static float R = 6372.8f;
    private final static float kilometresInAMile = 1.609344f;

    public static float inKilometres(float lat1, float lon1, float lat2, float lon2) {
        // TODO
        double radlat1 = lat1*Math.PI/180;
        double radlat2 = lat2*Math.PI/180;
        double radlon1 = lon1*Math.PI/180;
        double radlon2 = lon2*Math.PI/180;

        
        double a = Math.pow(Math.sin((radlat2-radlat1)/2),2)+ Math.cos(radlat1)*Math.cos(radlat2)*Math.pow(Math.sin((radlon2-radlon1)/2),2);
        double c = 2* Math.asin(Math.pow(a,0.5));
        double d = R * c;
        return (float) Math.round(d*10)/10;
    }

    public static float inMiles(float lat1, float lon1, float lat2, float lon2) {
        // TODO
        double radlat1 = lat1*Math.PI/180;
        double radlat2 = lat2*Math.PI/180;
        double radlon1 = lon1*Math.PI/180;
        double radlon2 = lon2*Math.PI/180;
        double R = 6372.8;

        double a = Math.pow(Math.sin((radlat2-radlat1)/2),2)+ Math.cos(radlat1)*Math.cos(radlat2)*Math.pow(Math.sin((radlon2-radlon1)/2),2);
        double c = 2* Math.asin(Math.pow(a,0.5));
        double d = R * c/kilometresInAMile ;
        return (float) Math.round(d*10)/10;
    }

}