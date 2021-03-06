package uk.ac.cam.cl.interaction_design.group1.backend;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Calendar;

import org.json.*;

public class WeatherApi {

	//Simplify JSON accesses
	private static JSONObject get(JSONObject o, String s) {
		return (JSONObject) o.get(s);
	}

	private static String getString(JSONObject o, String s) {
		return o.get(s).toString();
	}

	//Search for location using AccuWeather's Location API
	public static List<Location> searchLocation(String name) {
		List<Location> result = new ArrayList<Location>();
		String responseString = Request.getRequest("http://dataservice.accuweather.com/locations/v1/cities/search",
				new HashMap<String, String>() {
					{
						put("q", name);
					}
				});
		JSONArray response = new JSONArray(responseString);
		for (Object object : response) {
			JSONObject obj = (JSONObject) object;
			//Turn response into a list of locations
			Location location = new Location();
			location.name = getString(obj, "EnglishName");
			location.countryCode = getString(get(obj, "Country"), "ID");
			//Store county codes for countries with many cities
			if (location.countryCode.equals("US") || location.countryCode.equals("JM")) {
				location.countryCode = getString(get(obj, "AdministrativeArea"), "ID") + " (" + location.countryCode
						+ ")";
			}
			location.locationId = getString(obj, "Key");
			result.add(location);
		}
		return result;
	}

	public static Weather getWeatherForDay(int day) {
		String responseString = Request.getRequest("http://dataservice.accuweather.com/forecasts/v1/daily/5day/"
				+ LocationState.getCurrentLocation().locationId, new HashMap<String, String>() {
					{
						put("details", "true");
						put("metric", "true");
					}
				});

		//Get forecast to the given day
		JSONObject forecast = (JSONObject) ((JSONArray) (new JSONObject(responseString)).get("DailyForecasts"))
				.get(day);
		Weather result = new Weather();

		//Average temperature as the average of the maximum and the minimum
		result.temperature = (int) ((Double
				.parseDouble(getString(get(get(forecast, "Temperature"), "Minimum"), "Value"))
				+ Double.parseDouble(getString(get(get(forecast, "Temperature"), "Maximum"), "Value"))) / 2.0);

		result.windspeed = (int) Double
				.parseDouble(getString(get(get(get(forecast, "Day"), "Wind"), "Speed"), "Value"));

		//Reduce the many weather states provided by AccuWeather to the 4 we use
		int icon = Integer.parseInt(getString(get(forecast, "Day"), "Icon"));
		if (icon < 8 || (icon > 18 && icon < 24))
			result.rainLikelihood = Weather.RainEnum.UNLIKELY;
		else if (icon < 15)
			result.rainLikelihood = Weather.RainEnum.LIGHT_SHOWERS;
		else if (icon == 18)
			result.rainLikelihood = Weather.RainEnum.HEAVY_SHOWERS;
		else
			result.rainLikelihood = Weather.RainEnum.THUNDER;

		//Compute alerts
		if (Double.parseDouble(getString(get(get(forecast, "Day"), "Rain"), "Value")) > 0.0)
			result.alerts.add(new Weather.Alert("Rain expected!", "Bring a coat!"));
		if (Double.parseDouble(getString(get(get(forecast, "Day"), "Snow"), "Value")) > 0.0)
			result.alerts.add(new Weather.Alert("Snow expected!", "Cycle with caution!"));
		if (Double.parseDouble(getString(get(get(forecast, "Day"), "Ice"), "Value")) > 0.0)
			result.alerts.add(new Weather.Alert("Ice expected!", "Please don't cycle!"));

		//Fake humidity form hours of precipitation
		result.humidity = (int) (Double.parseDouble(getString(get(forecast, "Day"), "HoursOfPrecipitation")) / 0.12);

		result.sunrise = getString(get(forecast, "Sun"), "Rise").substring(11, 16);
		result.sunset = getString(get(forecast, "Sun"), "Set").substring(11, 16);

		return result;
	}

	public static Weather.GraphData getGraphData(int day) {
		Weather w = getWeatherForDay(day);
		Random rand = new Random(w.temperature * 1000 + w.windspeed * 2000 + w.humidity * 3000);
		Weather.GraphData result = new Weather.GraphData();

		//Generate random data
		for (int i = 0; i < 24; i++)
			result.temperature.add(w.temperature - 5 + rand.nextInt(10));
		for (int i = 0; i < 24; i++)
			result.windspeed.add(w.windspeed - 7 + rand.nextInt(14));

		//Random number of rain times based on humidity
		int rainNum = rand.nextInt(3) + w.humidity / 5;
		if (w.humidity == 0)
			rainNum = 0;
		//Generate random times
		for (int i = 0; i < rainNum; i++) {
			String hour = Integer.toString(rand.nextInt(24));
			while (hour.length() < 2)
				hour = "0" + hour;

			String minute = Integer.toString(rand.nextInt(24));
			while (minute.length() < 2)
				minute = "0" + minute;

			result.rainTimes.add(hour + ":" + minute);
		}

		return result;
	}

	//Get readable dates
	public static String getDate(int day) {
		String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day);
		return monthName[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DATE);
	}

}
