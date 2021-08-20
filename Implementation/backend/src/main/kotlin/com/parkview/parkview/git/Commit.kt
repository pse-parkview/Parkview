package com.parkview.parkview.git

import java.util.Date

/**
 * Class that represents a single commit for a given branch and benchmark type
 *
 * @param sha commit sha
 * @param message commit message
 * @param date commit date
 * @param author commit author
 */
data class Commit(
    val sha: String,
    val message: String = "",
    val date: Date = Date(),
    val author: String = "",
) {
    private val devices: MutableList<Device> = mutableListOf()
    val availableDevices: List<Device> get() = devices.toList()

    /**
     * Adds a device to this commit. This shows that the combination of commit and device has data available.
     *
     * @param device [Device] that gets added
     */
    fun addDevice(device: Device) {
        if (!devices.contains(device)) devices.add(device)
    }

    override fun equals(other: Any?): Boolean {
        if ((other is Commit) && (other.sha == sha)) return true

        return super.equals(other)
    }
}
