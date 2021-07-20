package com.parkview.parkview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppConfig::class)
class ParkviewApplication

fun main(args: Array<String>) {
    runApplication<ParkviewApplication>(*args)
}
