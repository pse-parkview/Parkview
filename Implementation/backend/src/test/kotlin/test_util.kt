import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Component
import com.parkview.parkview.benchmark.Conversion
import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.benchmark.ConversionDatapoint
import com.parkview.parkview.benchmark.Format
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.benchmark.Preconditioner
import com.parkview.parkview.benchmark.PreconditionerBenchmarkResult
import com.parkview.parkview.benchmark.PreconditionerDatapoint
import com.parkview.parkview.benchmark.Solver
import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.benchmark.SolverDatapoint
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import java.util.Date

fun BenchmarkResult.dirtyEquals(other: BenchmarkResult) = this.toString() == other.toString()

val COMMIT_A = Commit("commitasha", "", Date(), "")
val COMMIT_B = Commit("commitbsha", "", Date(), "")
val DEVICE = Device("gamer")

val COMMIT_A_RESULT = SpmvBenchmarkResult(
    COMMIT_A,
    DEVICE,
    (1..5).map {
        SpmvDatapoint(
            "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
            listOf(
                Format(name = "", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
            ),
        )
    }
)

val COMMIT_B_RESULT = SpmvBenchmarkResult(
    COMMIT_B,
    DEVICE,
    (1..5).map {
        SpmvDatapoint(
            "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
            listOf(
                Format(name = "", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
            ),
        )
    }
)

val SPMV_RESULT = SpmvBenchmarkResult(
    COMMIT_A,
    DEVICE,
    (1..5).map {
        SpmvDatapoint(
            "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
            listOf(
                Format(name = "", storage = 1, time = 1.0, maxRelativeNorm2 = 1.0, completed = true)
            ),
        )
    }
)

val SOLVER_RESULT = SolverBenchmarkResult(
    COMMIT_A,
    DEVICE,
    (1..5).map { index ->
        SolverDatapoint(
            "", index.toLong() * 10, index.toLong() * 10, index.toLong() * 10, "optimal",
            listOf(
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
    COMMIT_A,
    DEVICE,
    (1..5).map {
        ConversionDatapoint(
            "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
            listOf(
                Conversion("", 1.0, true),
            )
        )
    },
)

val BLAS_RESULT = BlasBenchmarkResult(
    COMMIT_A,
    DEVICE,
    (1..5).map {
        BlasDatapoint(
            it.toLong() * 10,
            operations = listOf(
                Operation("", 1.0, 1.0, it * 1.0, true),
            )
        )
    }
)

val PRECONDITIONER_RESULT = PreconditionerBenchmarkResult(
    COMMIT_A,
    DEVICE,
    (1..5).map {
        PreconditionerDatapoint(
            "", it.toLong() * 10, it.toLong() * 10, it.toLong() * 10,
            listOf(
                Preconditioner(
                    "",
                    listOf(
                        Component("", it.toDouble()),
                    ),
                    it.toDouble(),
                    listOf(
                        Component("", it.toDouble())
                    ),
                    it.toDouble(),
                    true
                )
            )
        )
    }
)
