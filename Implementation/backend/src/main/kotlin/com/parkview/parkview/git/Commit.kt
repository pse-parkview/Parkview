package com.parkview.parkview.git

import java.util.*

/**
 * Class that represents a single commit for a given branch and benchmark type
 *
 * @param sha commit sha
 * @param message commit message
 * @param date commit date
 */
data class Commit(
    val sha: String,
    val message: String,
    val date: Date,
    val author: String,
) {
    private val availableDevices: MutableList<Device> = mutableListOf()
    val device: List<Device> get() = availableDevices.toList()

    fun addDevice(device: Device) {
        availableDevices.add(device)
    }

}
