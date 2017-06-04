/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mindtrack.muslimorganizer.calculator.prayer;

/**
 * @author Usama
 */
public class PrayerTimesMath {

    public static double RTD = 180 / Math.PI;
    public static double DTR = Math.PI / 180;

    public static double fixAngle(double a) {
        a = a - 360.0 * (Math.floor(a / 360.0));
        a = a < 0 ? a + 360.0 : a;
        return a;
    }

    //degree Sin
    public static double dSin(double degreeAngle) {
        return Math.sin(degreeAngle * PrayerTimesMath.DTR);
    }

    //degree Cos
    public static double dCos(double degreeAngle) {
        return Math.cos(degreeAngle * PrayerTimesMath.DTR);
    }

    //degree Tan
    public static double dTan(double degreeAngle) {
        return Math.tan(degreeAngle * PrayerTimesMath.DTR);
    }

    //degree arcsin
    public static double dASin(double x) {
        
        return RTD * Math.asin(x);
    }

    //degree arccos
    public static double dACos(double x) {
        
        return RTD * Math.acos(x);
    }

    //degree arctan
    public static double dATan(double x) {
        
        return RTD * Math.atan(x);       
    }

    //degree arctan2
    public static double dATan2(double y, double x) {
        
        return RTD * Math.atan2(y, x);
    }

    //degree arccot
    public static double dACot(double x) {
        
        return 90 - dATan(x);
    }

    //fraction part of double variable
    public static double frac(double x) {
        return x - (int) x;
    }

}