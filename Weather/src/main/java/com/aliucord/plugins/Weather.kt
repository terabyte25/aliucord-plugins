package com.aliucord.plugins

import android.content.Context
import com.aliucord.Http
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.aliucord.plugins.weather.WeatherResponse
import com.discord.api.commands.ApplicationCommandType
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

@AliucordPlugin
class Weather : Plugin() {
    private val logger = Logger("Weather")
    private val thermometerEmoji = "\uD83C\uDF21"
    private val cloudEmoji = "☁️"
    private val humidityEmoji = "\uD83D\uDCA6"
    private val uvIndexEmoji = "\uD83D\uDD76️"
    private val windEmoji = "\uD83D\uDCA8"

    override fun start(context: Context) {
        val arguments = listOf(Utils.createCommandOption(ApplicationCommandType.STRING, "location", "The location to query"))

        commands.registerCommand("weather", "Get the weather for the current location, or a specific location", arguments) {
            val location = it.getString("location")
            val weather: WeatherResponse = try {
                Http.simpleJsonGet("http://wttr.in/" + (location
                        ?: "") + "?format=j1", WeatherResponse::class.java)
            } catch (throwable: Throwable) {
                logger.error(throwable)
                return@registerCommand CommandResult("Uh oh, failed to fetch weather data", null, false)
            }

            val condition = weather.current_condition[0]
            val weatherDesc = condition.weatherDesc[0]
            val nearestArea = weather.nearest_area[0]
            val areaName = nearestArea.areaName[0].value
            val country = nearestArea.country[0].value
            val region = nearestArea.region[0].value

            val embed = MessageEmbedBuilder().setTitle("Weather: ${weatherDesc.value}")
                    .setColor(0xEDEDED)
                    .setDescription(
                            "**Temp**:\n" +
                                    "$thermometerEmoji **Feels Like**: `${condition.FeelsLikeF}°F`  |  `${condition.FeelsLikeC}°C`\n" +
                                    "$thermometerEmoji **Temperature**: `${condition.temp_F}°F`  |  `${condition.temp_C}°C`\n" +
                                    "**Wind**:\n" +
                                    "$windEmoji **Wind Direction**: `${condition.winddir16Point}`\n" +
                                    "$windEmoji **Wind Speed**: `${condition.windspeedMiles} MPH`  |  `${condition.windspeedKmph} KMPH`\n" +
                                    "**Other**:\n" +
                                    "$cloudEmoji  **Cloud Cover**: `${condition.cloudcover}%`\n" +
                                    "$humidityEmoji **Humidity**: `${condition.humidity}`\n" +
                                    "$uvIndexEmoji **UV Index**: `${condition.uvIndex}`"
                    ).setFooter("$areaName, $region, $country", null)
            try {
                embed.setUrl("http://wttr.in/" + if (location == null) "" else URLEncoder.encode(location, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                logger.error(e)
            }
            CommandResult(null, listOf(embed.build()), false)
        }
    }

    override fun stop(context: Context) = commands.unregisterAll()
}