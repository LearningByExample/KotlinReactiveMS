package org.learning.by.example.reactive.kotlin.microservices.KotlinReactiveMS.model

data class GeoLocationResponse(var results: Array<GeoLocationResponse.Result> = arrayOf()
                               , var status: String = "") {

    data class Result(var address_components: Array<Result.Address_component> = arrayOf(),
                      var formatted_address: String = "",
                      var geometry: Result.Geometry = Result.Geometry(),
                      var place_id: String = "",
                      var types: Array<String> = arrayOf()) {

        data class Address_component(var long_name: String = "",
                                     var short_name: String = "",
                                     var types: Array<String> = arrayOf())

        data class Geometry(var bounds: Geometry.Bounds = Geometry.Bounds(),
                            var location: Geometry.Location = Geometry.Location(),
                            var location_type: String = "",
                            var viewport: Geometry.Viewport = Geometry.Viewport()) {

            data class Bounds(var northeast: Bounds.Northeast = Bounds.Northeast(),
                              var southwest: Bounds.Southwest = Bounds.Southwest()) {

                data class Northeast(var lat: Double = 0.0,
                                     var lng: Double = 0.0)

                data class Southwest(var lat: Double = 0.0,
                                     var lng: Double = 0.0)
            }

            data class Location(var lat: Double = 0.0,
                                var lng: Double = 0.0)

            data class Viewport(var northeast: Viewport.Northeast = Viewport.Northeast(),
                                var southwest: Viewport.Southwest = Viewport.Southwest()) {

                data class Northeast(var lat: Double = 0.0,
                                     var lng: Double = 0.0)

                data class Southwest(var lat: Double = 0.0,
                                     var lng: Double = 0.0)
            }
        }
    }
}
