## Kotlin Reactive Micro Services Example

## info
This is an example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2 using Kotlin.

This is a fork of the [original java version](https://github.com/LearningByExample/reactive-ms-example).

[![Build Status](https://travis-ci.org/LearningByExample/KotlinReactiveMS.svg?branch=master)](https://travis-ci.org/LearningByExample/KotlinReactiveMS)
[![codecov](https://codecov.io/gh/LearningByExample/KotlinReactiveMS/branch/master/graph/badge.svg)](https://codecov.io/gh/LearningByExample/KotlinReactiveMS)

_IntelliJ code coverage runner gives a 100%, unfortunately I've not managed to get the same results using codecov.io_

## W.I.P.
There is much code to be done to match the original java implementation

## Sample requests

Get hello
```shell
$ curl -X GET "http://localhost:8080/api/hello"
```

Will produce something like:
```json
{"hello":"world"}
```

## References
- https://github.com/LearningByExample/reactive-ms-example
- https://github.com/aesteve/todobackend-springboot2