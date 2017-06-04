/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mindtrack.muslimorganizer.calculator.prayer;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayList;
import java.util.Date;


/**
 * 
 * @author Usama
 */
@SuppressLint("SimpleDateFormat")
public class PrayerTimeCalculator {

	private double Fajr;
	private double Sunrise;
	private double Dhuhr;
	private double Asr;
	private double Sunset;
	private double Maghreb;
	private double Isha;
	private double Lng;
	private double Lat;
	private double Tzone;
	private int Mazhab;
	private int Way;
	private int Day;
	private int Month;
	private int Year;
	private int SumTimeZone = 0; // Summer time zone
	private double Eqt;
	private double Dcl;
	private double JDate;
	private double[] TimePortions;
	private Context context;


	public PrayerTimeCalculator(int day, int mnth, int yer, double lt,
			double lg, double tzone, int mz, int way, int DLSaving, Context context) {
		this.TimePortions = new double[] { 5 / 24.0, 6 / 24.0, 12 / 24.0,
				13 / 24.0, 18 / 24.0, 18 / 24.0, 18 / 24.0 };
		this.Day = day;
		this.Month = mnth;
		this.Year = yer;
		this.Lat = lt;
		this.Lng = lg;
		this.Tzone = tzone;
		this.Mazhab = mz;
		this.Way = way;
		this.SumTimeZone = DLSaving;
		this.JDate = this.calculateJulianDate(this.Day, this.Month, this.Year);
		this.context = context;
	}

	public void setSummerTZone(int V) {
		SumTimeZone = V;
	}

	private int isSummerTZone() {
		return SumTimeZone;
	}

	public double calculateJulianDate(int day, int month, int year) {

		if (month <= 2) {
			year -= 1;
			month += 12;
		}
		int A = year / 100;
		int B = A / 4;
		int C = 2 - A + B;
		int Dd = day;
		int Ee = (int) (365.25 * (year + 4716));
		int F = (int) (30.7 * (month + 1));

		double JD = C + Dd + Ee + F - 1524.5 - 1;

		return JD;
	}

	@SuppressWarnings("unused")
	public double calculateEquationOfTime(double JD, double TimePortion) {
		double d = JD - 2451545.0 + TimePortion;
		double g = MyMath.fixAngle(357.529 + 0.98560028 * d);
		double q = MyMath.fixAngle(280.459 + 0.98564736 * d);
		double L = q + 1.915 * (MyMath.dSin(g)) + 0.020 * (MyMath.dSin(2 * g));
		double R = 1.00014 - 0.01671 * (MyMath.dCos(g)) - 0.00014
				* (MyMath.dCos(2 * g));
		double e = 23.439 - 0.00000036 * d;
		double RA = MyMath.dATan2((MyMath.dCos(e)) * (MyMath.dSin(L)),
				(MyMath.dCos(L))) / 15;
		RA = this.fixHours(RA);
		double Eqt = q / 15 - RA;
		return Eqt;
	}

	// sun Declination
	@SuppressWarnings("unused")
	public double calculateSunDeclination(double JD, double TimePortion) {

		double d = JD - 2451545.0 + TimePortion;
		double g = 357.529 + 0.98560028 * d;
		double q = 280.459 + 0.98564736 * d;
		double L = q + 1.915 * (MyMath.dSin(g)) + 0.020 * (MyMath.dSin(2 * g));
		double R = 1.00014 - 0.01671 * (MyMath.dCos(g)) - 0.00014
				* (MyMath.dCos(2 * g));
		double e = 23.439 - 0.00000036 * d;
		double Decl = MyMath.dASin((MyMath.dSin(e)) * (MyMath.dSin(L)));
		return Decl;
	}

	// Dhuhr time calculation
	public double calculateDhuhrTime() {
		this.Eqt = this.calculateEquationOfTime(this.JDate,
				this.TimePortions[2]);
		double Dhuhr = this.fixHours(12 - this.Eqt);
		return Dhuhr;
	}

	public double calculateMidDay(double t) { // t is time portion
		this.Eqt = this.calculateEquationOfTime(this.JDate, t);
		double Z = this.fixHours(12 - this.Eqt);
		return Z;
	}

	// Sunrise and Sunset
	public double calculateT(double a, double TimePortion) {
		// time period between mid-day and the time at which sun reaches an
		// angle below the horizon
		this.Dcl = this.calculateSunDeclination(this.JDate, TimePortion);
		double A = MyMath.dSin(a);
		double B = (MyMath.dSin(this.Lat)) * (MyMath.dSin(this.Dcl));
		double C = (MyMath.dCos(this.Lat)) * (MyMath.dCos(this.Dcl));
		double D = (MyMath.dACos((-A - B) / C));
		double T1 = D / 15;
		return T1;
	}

	// Sunrise
	public double calculateSunrise() {
		double SRise = this.calculateMidDay(this.TimePortions[1])
				- this.calculateT(0.8333, this.TimePortions[1]);
		return SRise;
	}

	// Sunset & Maghreb prayer
	public double calculateSunset() {
		double SSet = this.calculateMidDay(this.TimePortions[5])
				+ this.calculateT(0.8333, this.TimePortions[4]);
		return SSet;
	}

	// Difference between mid-day and the time at which the object's shadow
	// equals k times
	public double calculateAofK(double G, double t) { // t is time portion
		double D = this.calculateSunDeclination(this.JDate, t);
		double Z = this.calculateMidDay(t);
		double V = ((double) (1 / 15.0))
				* MyMath.dACos((-MyMath.dSin(G) - MyMath.dSin(D)
						* MyMath.dSin(this.Lat))
						/ (MyMath.dCos(D) * MyMath.dCos(this.Lat)));
		return Z + (G > 90 ? -V : V);
	}

	// Asr Prayer
	public double calculateAsrTime() {
		double D = this.calculateSunDeclination(this.JDate,
				this.TimePortions[3]);
		double G = -MyMath.dACot(this.Mazhab + 1
				+ MyMath.dTan(Math.abs(this.Lat - D)));
		return this.calculateAofK(G, this.TimePortions[3]);
	}

	// Fajr and Isha
	public double[] calculateFajrAndIshaTimes() {
		double fajr = 0;
		double isha = 0;
		double[] FAI = new double[2];
		if (this.Lat <= 50) {
			switch (this.Way) {
			case 0: // Egyptian General Authority of Survey
				fajr = this.calculateMidDay(this.TimePortions[0])
				- this.calculateT(19.5, this.TimePortions[0]);
				isha = this.calculateMidDay(this.TimePortions[6])
						+ this.calculateT(17.5, this.TimePortions[6]);
				break;
			case 1: // Karachi
				fajr = this.calculateMidDay(this.TimePortions[0])
				- this.calculateT(18, this.TimePortions[0]);
				isha = this.calculateMidDay(this.TimePortions[6])
						+ this.calculateT(18, this.TimePortions[6]);
				break;
			case 2: // Islamic Society of North America (ISNA)
				fajr = this.calculateMidDay(this.TimePortions[0])
				- this.calculateT(15, this.TimePortions[0]);
				isha = this.calculateMidDay(this.TimePortions[6])
						+ this.calculateT(15, this.TimePortions[6]);
				break;
			case 3: // Umm al-Qura, Makkah
				fajr = this.calculateMidDay(this.TimePortions[0])
				- this.calculateT(18.5, this.TimePortions[0]);
				isha = this.Maghreb + 1.5;
				break;
			case 4: // Muslim World League(MWL)
				fajr = this.calculateMidDay(this.TimePortions[0])
				- this.calculateT(18, this.TimePortions[0]);
				isha = this.calculateMidDay(this.TimePortions[6])
						+ this.calculateT(17, this.TimePortions[6]);
				break;
			}
		} else // high latitude. One-Seventh of the Night method is used
		{
			// period between sunset-sunrise
			double P = 24 - (this.Sunset - this.Sunrise);
			fajr = this.Sunrise - 2 * P / 7.0 - 1 / 6.0;
			isha = this.Sunset + 2 * P / 7.0 - 1 / 15.0;

		}
		FAI[0] = fajr;
		FAI[1] = isha;
		return FAI;
	}

	public double[] calculatePrayersOneIteration() {
		this.Dhuhr = this.calculateDhuhrTime();
		this.Sunrise = this.calculateSunrise();
		this.Sunset = this.calculateSunset();
		this.Asr = this.calculateAsrTime();
		this.Maghreb = this.Sunset;
		double[] FAI = this.calculateFajrAndIshaTimes();
		this.Fajr = FAI[0];
		this.Isha = FAI[1];
		double[] Prayers = new double[6];
		Prayers[0] = this.Fajr;
		Prayers[1] = this.Sunrise;
		Prayers[2] = this.Dhuhr;
		Prayers[3] = this.Asr;
		Prayers[4] = this.Maghreb;
		Prayers[5] = this.Isha;
		return Prayers;
	}

	public double[] calculateDailyPrayers_withSunset() {
		double[] Prayers = this.calculatePrayersOneIteration();
		this.setTimePortions(Prayers);
		Prayers = this.calculatePrayersOneIteration();
		Prayers = this.adjustPrayers(Prayers);

		double unit = 60;

		double[] result = { Prayers[0] ,
				Prayers[1] + 0,
				Prayers[2] + 0,
				Prayers[3] + 0,
				Prayers[4] + 0,
				Prayers[5] + 0,
				24.00};

		return result;
	}

	// calculate prayer times for one definite day
	public double[] calculateDailyPrayers() {
		double[] Prayers = this.calculatePrayersOneIteration();
		this.setTimePortions(Prayers);
		Prayers = this.calculatePrayersOneIteration();
		Prayers = this.adjustPrayers(Prayers);
		double unit = 60;
		/*double fajrFactor = p.getFajrFactor() / unit;
		double thohrFactor = p.getThohrFactor() / unit;
		double asrfactor = p.getAsrFactor() / unit;
		double maghribFactor = p.getMaghrebFactor() / unit;
		double ishaFactor = p.getIshaFactor() / unit;*/

		double[] result = { Prayers[0] + 0,
				Prayers[2] + 0,
				Prayers[3] + 0,
				Prayers[4] + 0,
				Prayers[5] + 0};

		return result;
	}

	public ArrayList<String> calculateDailyPrayers_String() {

		double[] DPrayers = this.calculateDailyPrayers();
		ArrayList<String> SPrayers = new ArrayList<>();

		for (int i = 0; i < DPrayers.length; i++)
			SPrayers.add(i, this.adjustToString(DPrayers[i]));

		return SPrayers;
	}

	public ArrayList<String> calculateDailyPrayers_String_withSunrise() {

		double[] DPrayers = this.calculateDailyPrayers_withSunset();
		ArrayList<String> SPrayers = new ArrayList<>();

		for (int i = 0; i < DPrayers.length; i++)
			SPrayers.add(i, this.adjustToString(DPrayers[i]));

		return SPrayers;
	}
	
	public String adjustToString(double pt) {
		int hrs = (int) (pt);
		int min = MyMath.round((pt - hrs) * 60);
		int hrs_12 = (int) (pt);
		String mins = (min >= 10) ? "" + min : "0" + min;
		String hours = (hrs_12 >= 10) ? "" + hrs_12 : "0" + hrs_12;
		return hours + ":" + mins;
	}

	private void setTimePortions(double[] Prayers) {
		this.TimePortions[0] = Prayers[0] / 24.0;
		this.TimePortions[1] = Prayers[1] / 24.0;
		this.TimePortions[2] = Prayers[2] / 24.0;
		this.TimePortions[3] = Prayers[3] / 24.0;
		this.TimePortions[4] = Prayers[4] / 24.0;
		this.TimePortions[5] = Prayers[4] / 24.0;
		this.TimePortions[6] = Prayers[5] / 24.0;
	}

	private double fixHours(double a) {
		a = a - 24.0 * (Math.floor(a / 24.0));
		a = a < 0 ? a + 24.0 : a;
		return a;
	}

	private double[] adjustPrayers(double[] prayers) {
		for (int i = 0; i < prayers.length; i++) {
			prayers[i] = prayers[i] + this.Tzone - this.Lng / 15.0;
			prayers[i] = (Math.ceil(prayers[i] * 60)) / 60.0;
			if (this.isSummerTZone() == 1)
				prayers[i]++;
		}
		return prayers;
	}

	public String[][] calculateWeeklyPrayers(Date todayDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
