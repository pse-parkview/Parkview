// @ts-ignore
import * as parkview from "parkview-lib";
// @ts-ignore
import { kotlin } from "kotlin";

export class BenchmarkParser {
    parseBenchmarkResult(
        benchmark: any,
        commit: any,
        device: any,
        content: object[]
    ) {
        switch (benchmark) {
            case parkview.git.BenchmarkType.Spmv:
                return this.parseSpmv(commit, device, content);
            case parkview.git.BenchmarkType.Conversion:
                return this.parseConversion(commit, device, content);
            case parkview.git.BenchmarkType.Preconditioner:
                return this.parsePreconditioner(commit, device, content);
            case parkview.git.BenchmarkType.Solver:
                return this.parseSolver(commit, device, content);
            case parkview.git.BenchmarkType.Blas:
                return this.parseBlas(commit, device, content);
            default:
                throw Error(`Invalid benchmark type ${benchmark}`);
        }
    }

    private parseSpmv(commit: any, device: any, content: object[]) {
        return new parkview.benchmark.SpmvBenchmarkResult(
            commit,
            device,
            new kotlin.collections.ArrayList(
                content.map((e: any) => {
                    return new parkview.benchmark.SpmvDatapoint(
                        e.problem.name,
                        e.problem.rows,
                        e.problem.cols,
                        e.problem.nonzeros,
                        new kotlin.collections.ArrayList(
                            Object.entries(e.spmv).map((e: any) => {
                                return new parkview.benchmark.Format(
                                    e[0],
                                    e[1].time,
                                    e[1].completed,
                                    e[1].storage,
                                    e[1].max_relative_norm2
                                );
                            })
                        )
                    );
                })
            )
        );
    }

    private parseConversion(commit: any, device: any, content: object[]) {
        return new parkview.benchmark.ConversionBenchmarkResult(
            commit,
            device,
            new kotlin.collections.ArrayList(
                content.map((e: any) => {
                    return new parkview.benchmark.ConversionDatapoint(
                        e.problem.name,
                        e.problem.rows,
                        e.problem.cols,
                        e.problem.nonzeros,
                        new kotlin.collections.ArrayList(
                            Object.entries(e.spmv).map((e: any) => {
                                return new parkview.benchmark.Conversion(
                                    e[0],
                                    e[1].time,
                                    e[1].completed
                                );
                            })
                        )
                    );
                })
            )
        );
    }

    private parseSolver(commit: any, device: any, content: object[]) {
        return new parkview.benchmark.SolverBenchmarkResult(
            commit,
            device,
            new kotlin.collections.ArrayList(
                content.map((e: any) => {
                    return new parkview.benchmark.SolverDatapoint(
                        e.problem.name,
                        e.problem.rows,
                        e.problem.cols,
                        e.problem.nonzeros,
                        e.optimal.spmv,
                        new kotlin.collections.ArrayList(
                            Object.entries(e.solver).map((e: any) => {
                                return new parkview.benchmark.Solver(
                                    e[0],
                                    new kotlin.collections.ArrayList(e[1].recurrent_residuals),
                                    new kotlin.collections.ArrayList(e[1].true_residuals),
                                    new kotlin.collections.ArrayList(e[1].implicit_residuals),
                                    new kotlin.collections.ArrayList(e[1].iteration_timestamps),
                                    e[1].rhs_norm,
                                    e[1].residual_norm,
                                    e[1].completed,
                                    new kotlin.collections.ArrayList(
                                        Object.entries(e[1].generate.components).map(
                                            (c: any) => new parkview.benchmark.Component(c[0], c[1])
                                        )
                                    ),
                                    e[1].generate.time,
                                    new kotlin.collections.ArrayList(
                                        Object.entries(e[1].apply.components).map(
                                            (c: any) => new parkview.benchmark.Component(c[0], c[1])
                                        )
                                    ),
                                    e[1].apply.time,
                                    e[1].apply.iterations
                                );
                            })
                        ),
                        new kotlin.collections.ArrayList(
                            Object.entries(e.spmv).map((e: any) => {
                                return new parkview.benchmark.Format(
                                    e[0],
                                    e[1].time,
                                    e[1].completed,
                                    e[1].storage,
                                    e[1].max_relative_norm2
                                );
                            })
                        )
                    );
                })
            )
        );
    }

    // i dont believe in its existence, will implement it as soon as i've got testdata
    private parsePreconditioner(commit: any, device: any, content: object[]) {}

    private parseBlas(commit: any, device: any, content: object[]) {
        return new parkview.benchmark.BlasBenchmarkResult(
            commit,
            device,
            new kotlin.collections.ArrayList(
                content.map((e: any) => {
                    return new parkview.benchmark.BlasDatapoint(
                        e.n,
                        e.r,
                        e.m,
                        e.k,
                        new kotlin.collections.ArrayList(
                            Object.entries(e.blas).map((e: any) => {
                                return new parkview.benchmark.Operation(
                                    e[0],
                                    e[1].time,
                                    e[1].flops,
                                    e[1].bandwidth,
                                    e[1].completed,
                                    e[1].repetitions
                                );
                            })
                        )
                    );
                })
            )
        );
    }
}