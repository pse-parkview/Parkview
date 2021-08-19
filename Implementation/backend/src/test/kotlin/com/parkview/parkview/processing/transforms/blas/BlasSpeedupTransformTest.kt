package com.parkview.parkview.processing.transforms.blas

import COMMIT_A
import DEVICE
import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.processing.transforms.PointDataset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BlasSpeedupTransformTest {
    private val plot = BlasSpeedupTransform()

    private val benchmark = BlasBenchmarkResult(
        COMMIT_A,
        DEVICE,
        (1..10).map {
            BlasDatapoint(it.toLong() * 1,
                it.toLong() * 2,
                it.toLong() * 3,
                it.toLong() * 4,
                listOf(Operation("", 1.0, 1.0, it * 1.0, true))
            )
        },
    )

    @Test
    fun transformBlas() {
        val options = mutableMapOf(
            "compare" to "${benchmark.identifier}/${benchmark.identifier}",
            "xAxis" to "n",
        )
        val data = plot.transformBlas(listOf(benchmark, benchmark), options)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}