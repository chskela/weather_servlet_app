package api.config

object Config {
    val weatherApiKey: String = System.getenv("WEATHER_API_KEY")
}