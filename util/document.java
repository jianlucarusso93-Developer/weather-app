/**
 * This program is a simple weather application that uses online APIs.
 *
 * How it works:
 * 1. The user enters the name of a city (in any language).
 * 2. The program sends a request to a geocoding API to find matching cities.
 * 3. If multiple cities are found, the user can choose one from a list.
 * 4. The program retrieves the latitude and longitude of the selected city.
 * 5. It then sends another request to a weather API to get current weather data.
 * 6. Finally, it displays:
 *    - City name
 *    - Region (if available)
 *    - Country
 *    - Current temperature
 *    - Weather condition (e.g., clear sky, rain, etc.)
 *
 * Features:
 * - Handles multiple city results
 * - Supports cities with spaces (e.g., "New York")
 * - Includes basic error handling (invalid input, city not found, network issues)
 * - Uses JSON parsing with Gson
 *
 * This project is designed for beginners to learn:
 * - How to call REST APIs in Java
 * - How to handle JSON data
 * - Basic error handling
 * - User input management
 *
 * Requirements:
 * - Internet connection
 * - Gson library included in the project
 */