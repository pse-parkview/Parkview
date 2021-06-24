package com.parkview.parkview.benchmark

/**
 * A single datapoint, contains the problem description (n, r, m, k) and
 * a list of operations.
 *
 * @param n the given n value
 * @param r the given r value, defaults to 1
 * @param m the given m value, defaults to n
 * @param k the given k value, defaults to n
 * @param operations list of operations
 */
class BlasDatapoint(
    val n: Int,
    val r: Int = 1,
    val m: Int = n,
    val k: Int = n,
    val operations: List<Operation>
)