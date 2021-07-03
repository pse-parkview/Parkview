package com.parkview.parkview.processing

import org.junit.jupiter.api.Test

internal class PointListTest {
    @Test
    fun `test toJson() for single point`() {
        val data = PointList(listOf(LabeledPoint(0.0, 0.0, "test")))

        val result = "[{\"x\":0.0,\"y\":0.0,\"label\":\"test\"}]"

        assert(data.toJson() == result)
    }

    @Test
    fun `test toJson() for list of points`() {
        val data = PointList((1..5).map { i -> LabeledPoint(i.toDouble(), i.toDouble(), i.toString()) })

        val result = (1..5).joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) { i -> "{\"x\":${i.toDouble()},\"y\":${i.toDouble()},\"label\":\"$i\"}" }

        assert(data.toJson() == result)
    }
}