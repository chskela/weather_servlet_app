package exception

class WeatherApiException(message: String) : RuntimeException("Weather API error: $message")