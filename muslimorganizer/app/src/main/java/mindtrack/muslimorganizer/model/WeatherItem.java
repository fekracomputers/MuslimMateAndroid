package mindtrack.muslimorganizer.model;


public class WeatherItem {

	int id;
	long timestamp;
	int max;
	int min;
	String icon;

	public WeatherItem() {
		this.timestamp = 0;
		this.max = 0;
		this.min = 0;
		this.icon = "01d";
	}

	public WeatherItem(int id,long timestamp, int max, int min, String icon) {
		super();
		this.timestamp = timestamp;
		this.max = max;
		this.min = min;
		this.icon = icon;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}

}
