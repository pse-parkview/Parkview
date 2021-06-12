package com.parkview.parkview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParkviewApplication

fun main(args: Array<String>) {
	runApplication<ParkviewApplication>(*args)
}
