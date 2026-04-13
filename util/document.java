/**
 * Weather application that retrieves real-time weather data
 * using the Open-Meteo API.
 *
 * <p>
 * HOW IT WORKS:
 * 1. The program asks the user to enter a city name via console (Scanner).
 * 2. It sends a request to the Open-Meteo Geocoding API to find matching cities.
 * 3. If multiple cities are found, the user selects one by index.
 * 4. The selected city's latitude and longitude are extracted.
 * 5. A second API request is sent to Open-Meteo Forecast API.
 * 6. The program retrieves current temperature and weather condition.
 * 7. A weather code is converted into a human-readable description.
 * 8. The final result is printed in the console.
 * </p>
 *
 * FEATURES:
 * - Supports multiple city results selection
 * - Automatic retry mechanism for API requests
 * - Handles network errors and invalid inputs
 * - Converts weather codes into readable descriptions
 *
 * EXTERNAL API USED:
 * - https://geocoding-api.open-meteo.com
 * - https://api.open-meteo.com
 *
 * NOTE:
 * This version is console-based and uses Scanner for input.
 * It can be adapted to a web application using Spring Boot or JavaScript.
 */