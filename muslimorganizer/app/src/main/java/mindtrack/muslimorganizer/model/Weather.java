package mindtrack.muslimorganizer.model;

/**
 * Model class for weather
 */
public class Weather {
    public String dayName, temp, tempMini, tempMax, image, desc, humidity, windSpeed;

    public Weather(String dayName, String temp, String tempMini, String tempMax, String image, String desc, String humidity, String windSpeed) {
        this.dayName = dayName;
        this.temp = temp;
        this.tempMini = tempMini;
        this.tempMax = tempMax;
        this.image = image;
        this.desc = desc;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public Weather(String dayName, String tempMini, String tempMax, String image) {
        this.dayName = dayName;
        this.tempMini = tempMini;
        this.tempMax = tempMax;
        this.image = image;
    }

}
