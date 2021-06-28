-- somebody should check this
CREATE SCHEMA IF NOT EXISTS parkview ;

CREATE TABLE IF NOT EXISTS parkview.Commit (
  sha VARCHAR(40) NOT NULL,
  message VARCHAR(45) NULL,
  PRIMARY KEY (sha)) ;

CREATE TABLE IF NOT EXISTS parkview.BenchmarkResult (
  idBenchmarkResult VARCHAR(45) NOT NULL,
  sha VARCHAR(40) NOT NULL,
  deviceName VARCHAR(45) NULL,
  summaryValue DOUBLE PRECISION NULL,
  description VARCHAR(200) NULL,
  benchmarkType VARCHAR(45) NULL,
  PRIMARY KEY (idBenchmarkResult),
  CONSTRAINT sha
    FOREIGN KEY (sha)
    REFERENCES parkview.Commit(sha)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX sha_idx ON parkview.BenchmarkResult (sha ASC) ;

CREATE TABLE IF NOT EXISTS parkview.BlasBenchmark (
  idBlasBenchmark VARCHAR(45) NOT NULL,
  idBenchmarkResult VARCHAR(45) NOT NULL,
  n INT NULL,
  m INT NULL,
  r INT NULL,
  k INT NULL,
  PRIMARY KEY (idBlasBenchmark),
  CONSTRAINT idBenchmarkResult
    FOREIGN KEY (idBenchmarkResult)
    REFERENCES parkview.BenchmarkResult(idBenchmarkResult)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX idBenchmarkResult_blas_idx ON parkview.BlasBenchmark (idBenchmarkResult ASC) ;

CREATE TABLE IF NOT EXISTS parkview.operations (
  idBlasBenchmark VARCHAR(45) NOT NULL,
  opName VARCHAR(45) NOT NULL,
  opTime DOUBLE PRECISION NULL,
  opFlops DOUBLE PRECISION NULL,
  opBandwidth DOUBLE PRECISION NULL,
  CONSTRAINT idBlasBenchmark
    FOREIGN KEY (idBlasBenchmark)
    REFERENCES parkview.BlasBenchmark (idBlasBenchmark)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX idBlasBenchmark_idx ON parkview.operations (idBlasBenchmark ASC) ;

CREATE TABLE IF NOT EXISTS parkview.matrixBenchmark (
  idMatrixBenchmark VARCHAR(45) NOT NULL,
  idBenchmarkResult VARCHAR(45) NOT NULL,
  matFileName VARCHAR(45) NULL,
  matRows INT NULL,
  matColumns INT NULL,
  matNonZero INT NULL,
  matType VARCHAR(45) NULL,
  PRIMARY KEY (idMatrixBenchmark),
	CONSTRAINT idBenchmarkResult
    FOREIGN KEY (idBenchmarkResult)
    REFERENCES parkview.BenchmarkResult(idBenchmarkResult)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX idBenchmarkResult_matrix_idx ON parkview.matrixBenchmark (idBenchmarkResult ASC) ;
  
CREATE TABLE IF NOT EXISTS parkview.Conversions (
  idMatrixBenchmark VARCHAR(45) NOT NULL,
  conName VARCHAR(45) NULL,
  conTime VARCHAR(45) NULL,
  conCompleted SMALLINT NULL,
  Conversionscol VARCHAR(45) NULL,
  CONSTRAINT idMatrixBenchmark
    FOREIGN KEY (idMatrixBenchmark)
    REFERENCES parkview.matrixBenchmark (idMatrixBenchmark)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX idMatrixBenchmark_conversion_idx ON parkview.Conversions (idMatrixBenchmark ASC) ;

CREATE TABLE IF NOT EXISTS parkview.spmvBenchmark (
  idMatrixBenchmark VARCHAR(45) NOT NULL,
  optimal VARCHAR(45) NULL,
  spmvFormatName VARCHAR(45) NULL,
  spmvFormatStorage INT NULL,
  spmvFormatMaxRelativeNorm2 DOUBLE PRECISION NULL,
  spmvFormatCompleted SMALLINT NULL,
  CONSTRAINT idMatrixBenchmark
    FOREIGN KEY (idMatrixBenchmark)
    REFERENCES parkview.matrixBenchmark (idMatrixBenchmark)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;
  
CREATE INDEX idMatrixBenchmark_spmv_idx ON parkview.spmvBenchmark (idMatrixBenchmark ASC) ;
  
CREATE TABLE IF NOT EXISTS parkview.SolverBenchmark (
  idMatrixBenchmark VARCHAR(45) NOT NULL,
  solverName VARCHAR(45) NULL,
  solverRecurrentResiduals DOUBLE PRECISION NULL,
  solverTrueResidulas DOUBLE PRECISION NULL,
  solverImplicitResiduals DOUBLE PRECISION NULL,
  solverIterationTimestamps INT NULL,
  solverRhsNorm DOUBLE PRECISION NULL,
  solverResidualNorm DOUBLE PRECISION NULL,
  solverCompleted SMALLINT NULL,
  ´solverGenerateTotalTime DOUBLE PRECISION NULL,
  solverApplyTotalTime DOUBLE PRECISION NULL,
  soverApplyIterations INT NULL,
  CONSTRAINT idMatrixBenchmark
    FOREIGN KEY (idMatrixBenchmark)
    REFERENCES parkview.matrixBenchmark (idMatrixBenchmark)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;
	
CREATE INDEX idMatrixBenchmark_solver_idx ON parkview.SolverBenchmark (idMatrixBenchmark ASC) ;

CREATE TABLE IF NOT EXISTS parkview.Components (
  idMatrixBenchmark VARCHAR(45) NOT NULL,
  componentName VARCHAR(45) NULL,
  componentRuntime DOUBLE PRECISION NULL,
  componentApply SMALLINT NULL,
  CONSTRAINT idMatrixBenchmark
    FOREIGN KEY (idMatrixBenchmark)
    REFERENCES parkview.matrixBenchmark (idMatrixBenchmark)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;
	
CREATE INDEX idMatrixBenchmark_component_idx ON parkview.Components (idMatrixBenchmark ASC) ;

CREATE TABLE IF NOT EXISTS parkview.PreconditionerBenchmark (
  idMatrixBenchmark VARCHAR(45) NOT NULL,
  PreconName VARCHAR(45) NULL,
  PreconGenerateTime DOUBLE PRECISION NULL,
  PreconApplyTime DOUBLE PRECISION NULL,
  PreconCompleted SMALLINT NULL,
  CONSTRAINT idMatrixBenchmark
    FOREIGN KEY (idMatrixBenchmark)
    REFERENCES parkview.matrixBenchmark (idMatrixBenchmark)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX idMatrixBenchmark_preconditioner_idx ON parkview.PreconditionerBenchmark (idMatrixBenchmark ASC) ;

CREATE TABLE IF NOT EXISTS parkview.Branch (
  branchName VARCHAR(80) NOT NULL,
  PRIMARY KEY (branchName)) ;

CREATE TABLE IF NOT EXISTS parkview.BranchCommits (
  branchName VARCHAR(80) NOT NULL,
  sha VARCHAR(40) NOT NULL,
  parentsha VARCHAR(40) NOT NULL,
  CONSTRAINT sha
    FOREIGN KEY (sha)
    REFERENCES parkview.Commit (sha)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT branchName
    FOREIGN KEY (branchName)
    REFERENCES parkview.Branch (branchName)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT parentsha
    FOREIGN KEY (sha)
    REFERENCES parkview.Commit (sha)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ;

CREATE INDEX branch_commit_idx ON parkview.BranchCommits (sha ASC, branchName) ;


