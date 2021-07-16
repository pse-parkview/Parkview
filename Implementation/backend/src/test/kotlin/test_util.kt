import com.parkview.parkview.benchmark.*
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import java.util.*

fun BenchmarkResult.dirtyEquals(other: BenchmarkResult) = this.toString() == other.toString()

private val commit = Commit("sha", "", Date(), "")
private val device = Device("gamer")

val SPMV_RESULT = SpmvBenchmarkResult(
    commit,
    device,
    BenchmarkType.Spmv,
    (1..5).map {
        val format = Format(name = "", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
        SpmvDatapoint(
            it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
            listOf(
                format
            ),
        )
    }
)

val SOLVER_RESULT = SolverBenchmarkResult(
    commit,
    device,
    BenchmarkType.Solver,
    (1..5).map { index ->
        SolverDatapoint(
            index.toLong() * 10, index.toLong() * 10, index.toLong() * 10, listOf(
                Solver(
                    "",
                    generateComponents = listOf(Component("", 1.0)),
                    generateTotalTime = 1.0,
                    recurrentResiduals = (1..100).map { it.toDouble() },
                    applyComponents = listOf(Component("", 1.0)),
                    applyIterations = index.toLong() * 10,
                    applyTotalTime = 1.0,
                    completed = true
                )
            )
        )
    }
)


val CONVERSION_RESULT = ConversionBenchmarkResult(
    commit,
    device,
    BenchmarkType.Conversion,
    (1..5).map {
        ConversionDatapoint(
            it.toLong() * 10, it.toLong() * 10, it.toLong() * 10, listOf(
                Conversion("", 1.0, true),
            )
        )
    },
)

val BLAS_RESULT = BlasBenchmarkResult(
    commit,
    device,
    BenchmarkType.Blas,
    (1..5).map {
        BlasDatapoint(
            10, operations = listOf(
                Operation("", 1.0, 1.0, it * 1.0, true),
            )
        )
    }
)

val PRECONDITIONER_RESULT = PreconditionerBenchmarkResult(
    commit,
    device,
    BenchmarkType.Preconditioner,
    (1..5).map {
        PreconditionerDatapoint(
            10, 10, 10, listOf(
                Preconditioner(
                    "", listOf(
                        Component("", it.toDouble()),
                    ),
                    it.toDouble(), listOf(
                        Component("", it.toDouble())
                    ), it.toDouble(), true
                )
            )
        )
    }
)
