async function searchCity() {

    const city = document.getElementById("city").value.trim();

    if (!city) {
        alert("Please enter a city");
        return;
    }

    try {
        // ---------------- GEO (equivalente Java HttpClient) ----------------
        const geoUrl =
            `https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(city)}&language=en`;

        const geoResponse = await fetch(geoUrl);
        const geoData = await geoResponse.json();

        if (!geoData.results || geoData.results.length === 0) {
            document.getElementById("result").innerHTML = "❌ City not found";
            return;
        }

        let results = geoData.results;

        let choice = 0;

        // ---------------- MULTIPLE CITIES ----------------
        if (results.length > 1) {

            let html = "<h3>Select a city:</h3>";

            results.forEach((c, i) => {
                const region = c.admin1 || "Unknown";

                html += `
                    <button onclick="selectCity(
                        ${i}
                    )">
                        ${c.name} - ${region} (${c.country})
                    </button><br>
                `;
            });

            window._results = results; // salva risultati globalmente
            document.getElementById("result").innerHTML = html;
            return;
        }

        showWeather(results[0]);

    } catch (err) {
        document.getElementById("result").innerHTML = "❌ Error";
    }
}

// ---------------- SELEZIONE CITTÀ ----------------
function selectCity(index) {
    const city = window._results[index];
    showWeather(city);
}

// ---------------- WEATHER (equivalente secondo fetch Java) ----------------
async function showWeather(city) {

    const lat = city.latitude;
    const lon = city.longitude;

    const weatherUrl =
        `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,weathercode`;

    const weatherResponse = await fetch(weatherUrl);
    const weatherData = await weatherResponse.json();

    const temp = weatherData.current.temperature_2m;
    const code = weatherData.current.weathercode;

    const description = getWeatherDescription(code);

    document.getElementById("result").innerHTML = `
        <h2>${city.name}</h2>
        <p>${city.admin1 || "Unknown"} - ${city.country}</p>
        <h3>🌡️ ${temp}°C</h3>
        <p>${description}</p>
    `;
}

// ---------------- WEATHER DESCRIPTION (uguale Java switch) ----------------
function getWeatherDescription(code) {

    switch (code) {
        case 0: return "Clear sky";
        case 1:
        case 2:
        case 3: return "Partly cloudy";
        case 45:
        case 48: return "Fog";
        case 51:
        case 53:
        case 55: return "Drizzle";
        case 61:
        case 63:
        case 65: return "Rain";
        case 71:
        case 73:
        case 75: return "Snow";
        case 80:
        case 81:
        case 82: return "Rain showers";
        case 95: return "Thunderstorm";
        default: return "Unknown";
    }
}