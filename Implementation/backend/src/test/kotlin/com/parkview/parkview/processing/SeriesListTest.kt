package com.parkview.parkview.processing

import org.junit.jupiter.api.Test

internal class SeriesListTest {
    @Test
    fun `test toJson() for single point`() {
        val data = SeriesList(listOf(Series("", listOf(Point("", 0.0)))))

        val result = "[{\"name\":\"\",\"series\":[{\"name\":\"\",\"value\":0.0}]}]"

        print(data.toJson())
        assert(data.toJson() == result)
    }

    @Test
    fun `test toJson() for list of points`() {
        val data = SeriesList((1..5).map { i -> Series(i.toString(), listOf(Point(i.toString(), i.toDouble()))) })

        val result = (1..5).joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) { i -> "{\"name\":\"$i\",\"series\":[{\"name\":\"$i\",\"value\":${i.toDouble()}}]}" }

        assert(data.toJson() == result)
    }
}