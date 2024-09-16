package mypackage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


 // Servlet implementation class WeatherServlet

@WebServlet("/WeatherServlet")
public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public WeatherServlet()  {
        super();
}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		response.sendRedirect("index.html");
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//API Key
		String apiKey = "aa8e83372d232ec85df3144dbf6381f0";
		// Get the city from the form input
        String city = request.getParameter("city"); 

        // Create the URL for the OpenWeatherMap API request
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

        try {
        	//api integration
           URL url=new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //reading the data from network
           InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                
                 StringBuilder responseContent = new StringBuilder();
                Scanner scanner = new Scanner(reader);

                while (scanner.hasNext()) {
                    responseContent.append(scanner.nextLine());
                }
                
               scanner.close();
                // System.out.println(responseContent);
                
                // Parse the JSON response to extract temperature, date, and humidity
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
                 System.out.println(jsonObject);
                //Date & Time
                long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
                String date = new Date(dateTimestamp).toString();
                
                //Temperature
                double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                int temperatureCelsius = (int) (temperatureKelvin - 273.15);
               
                //Humidity
                int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
                
                //Wind Speed
                double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                
                //Weather Condition
                String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
                
                // Set the data as request attributes (for sending to the jsp page)
                request.setAttribute("date", date);
                request.setAttribute("city", city);
                request.setAttribute("temperature", temperatureCelsius);
                request.setAttribute("weatherCondition", weatherCondition); 
                request.setAttribute("humidity", humidity);    
                request.setAttribute("windSpeed", windSpeed);
                request.setAttribute("weatherData", responseContent.toString());
                
                connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}