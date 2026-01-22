package myPackages;

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
import java.net.URLEncoder;
import java.sql.Date;
import java.util.Scanner;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//API setup
		String apiKey = "Put_ur_Api_key_here";
		String city = URLEncoder.encode(request.getParameter("city"), "UTF-8"); 
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
		        + city + "&units=metric&appid=" + apiKey;

       try { 
        //API Integration
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
		
        //Reading the data from Network
        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
		
        //want to store in string
        StringBuilder responseContent = new StringBuilder();
        
        //Input from reader
        Scanner scanner = new Scanner(reader);
        
        while(scanner.hasNext()) {
        	responseContent.append(scanner.nextLine());
        }
		
		scanner.close();
		
		//Typecasting(Parsing)
		Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
        //System.out.println(jsonObject);
		
        //Date & Time
        long timestamp = jsonObject.get("dt").getAsLong();
        int timezoneOffset = jsonObject.get("timezone").getAsInt();

        Instant instant = Instant.ofEpochSecond(timestamp);

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm:ss")
                .withZone(ZoneId.of("Asia/Kolkata"));

        String dateTime = formatter.format(instant);
        
        //Temperature
        int temperatureCelsius = jsonObject.getAsJsonObject("main").get("temp").getAsInt();

        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        
    	// Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("timestamp", timestamp);
        request.setAttribute("timezoneOffset", timezoneOffset);
        request.setAttribute("dateTime", dateTime);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        
        connection.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
        request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
