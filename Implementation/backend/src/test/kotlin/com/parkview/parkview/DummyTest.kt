package com.parkview.parkview

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.rest.ParkviewApiHandler
import kotlin.test.Test
import kotlin.test.assertEquals

class DummyTest {
    @Test
    fun test_dummy_get_devices() {
        val db = DummyDatabaseHandler()
        val rp = DummyRepositoryHandler()
        val rest = ParkviewApiHandler(repHandler = rp, databaseHandler = db)

        val device = rest.getAvailableDevices("test", BenchmarkType.Spmv).first()
        assertEquals(device, DUMMY_DEVICE)
    }
}