## Kotlin Reactive Micro Services Example

## info
This is an example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2 using Kotlin.

This is a fork of the [original java version](https://github.com/LearningByExample/reactive-ms-example).

[![Build Status](https://travis-ci.org/LearningByExample/KotlinReactiveMS.svg?branch=master)](https://travis-ci.org/LearningByExample/KotlinReactiveMS)
[![codecov](https://codecov.io/gh/LearningByExample/KotlinReactiveMS/branch/master/graph/badge.svg)](https://codecov.io/gh/LearningByExample/KotlinReactiveMS)

_IntelliJ code coverage runner gives a 100%, unfortunately I've not managed to get the same results using codecov.io_

This service provide and API that will get the geo location and the sunrise and sunset times from an address.

```Gherkin
Scenario: Get Location
  Given I've an address
  When I call the location service
  Then I should get a geo location
  And I should get the sunrise and sunset times
```
To implement this example we consume a couple of REST APIs.

This example cover several topics: 

- Functional programing.
- Reactive types.
- Router Functions.
- ~~Static Web-Content~~.
- Creation on Reactive Java Services/Components.
- Error handling in routes and services.
- Reactive Web Client to consume external REST Services.
- Organizing your project in manageable packaging.

Includes and in depth look to testing using JUnit5:
- Unit, Integration and System tests.
- Mocking, including reactive functions and JSON responses.
- BDD style assertions.
- Test tags with maven profiles.

## W.I.P.
There is much code to be done to match the original java implementation

## Sample requests

Get from address
```shell
$ curl -X GET "http://localhost:8080/api/location/Trafalgar%20Square%2C%20London%2C%20England" -H  "accept: application/json"
```

Post from JSON
```shell
$ curl -X POST "http://localhost:8080/api/location" -H  "accept: application/json" -H  "content-type: application/json" -d "{  \"address\": \"Trafalgar Square, London, England\"}"
```

Both will produce something like:
```json
{
  "geographicCoordinates": {
    "latitude": 51.508039,
    "longitude": -0.128069
  },
  "sunriseSunset": {
    "sunrise": "2017-05-21T03:59:08+00:00",
    "sunset": "2017-05-21T19:55:11+00:00"
  }
}
```
_All date and times are ISO 8601 UTC without summer time adjustment_

## API
[![Run in Postman](https://lh4.googleusercontent.com/Dfqo9J42K7-xRvHW3GVpTU7YCa_zpy3kEDSIlKjpd2RAvVlNfZe5pn8Swaa4TgCWNTuOJOAfwWY=s20) Run in Postman](https://app.getpostman.com/run-collection/498aea143dc572212f17)

## Project Structure

- [main/kotlin](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS)
    - [/application](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/application) : Main Spring boot application and context configuration.  
    - [/extensions](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/extensions) : Custom kotlin extensions and high level functions.
    - [/routers](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/routers) : Reactive routing functions.
    - [/handlers](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/handlers) : Handlers used by the routers.
    - [/services](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/services) : Services for the business logic needed by handlers.
    - [/exceptions](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/exceptions) : Businesses exceptions.
    - [/model](/src/main/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/model) : POJOs.
- [test/kotlin](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS)
    - [/application](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/application) : Application system and unit tests.
    - [/extensions](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/extensions) : Extensions unit tests.
    - [/routers](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/routers) : Integration tests for routes.
    - [/handlers](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/handlers) : Unit tests for handlers.
    - [/services](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/services) : Unit tests for services.
    - [/model](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/model) : POJOs used by the test.
    - [/test](/src/test/kotlin/org/learning/by/example/reactive/kotlin/microservices/KotlinReactiveMS/test) : Helpers and base classes for testing.


## References
- https://github.com/LearningByExample/reactive-ms-example
- https://github.com/aesteve/todobackend-springboot2