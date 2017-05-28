package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model

data class GeoTimesResponse(var results: GeoTimesResponse.Results = GeoTimesResponse.Results(),
                            var status: String = "") {

    data class Results(var sunrise: String = "",
                       var sunset: String = "",
                       var solar_noon: String = "",
                       var day_length: Long = 0,
                       var civil_twilight_begin: String = "",
                       var civil_twilight_end: String = "",
                       var nautical_twilight_begin: String = "",
                       var nautical_twilight_end: String = "",
                       var astronomical_twilight_begin: String = "",
                       var astronomical_twilight_end: String = "")
}
