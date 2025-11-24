object Pipeline {
  def apply[T](block: => T): LazyPipeline[T] = new LazyPipeline(block)
}

class LazyPipeline[T](block: => T) {
  // Lazy — executes only when accessed first time
  lazy val result: T = block

  // map builds a new lazy stage in the pipeline
  def map[R](f: T => R): LazyPipeline[R] = Pipeline {
    f(result)  // triggers previous lazy stage only when needed
  }
}

object LazyPipelineBuilder {
  def main(args: Array[String]): Unit = {
    val p = Pipeline {
      println("Step 1: Preparing data")
      List(1, 2, 3)
    }.map { xs =>
      println("Step 2: Transforming data")
      xs.map(_ * 2)
    }

    println("Before accessing pipeline...")
    println("Result: " + p.result)
  }
}
