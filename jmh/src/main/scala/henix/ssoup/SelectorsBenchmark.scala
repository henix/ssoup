package henix.ssoup

import java.util.concurrent.TimeUnit

import org.jsoup.Jsoup
import org.openjdk.jmh.annotations._

import scala.io.Source

@State(Scope.Benchmark)
class SelectorsBenchmark {

  private val document = {
    val in = Source.fromURL(classOf[SelectorsBenchmark].getResource("/B00796PHOC.html"))
    val doc = Jsoup.parse(in.mkString)
    in.close()
    doc
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def dfsTraverse(): Unit = {
    val iter = Selectors.dfsTraverse(document)
    while (iter.hasNext) {
      iter.next()
    }
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def bfsTraverse(): Unit = {
    val iter = Selectors.bfsTraverse(document)
    while (iter.hasNext) {
      iter.next()
    }
  }
}
