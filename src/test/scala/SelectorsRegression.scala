package henix.ssoup

import org.jsoup.Jsoup
import org.scalameter.api.Gen
import org.scalameter.PerformanceTest

import scala.io.Source

class SelectorsRegression extends PerformanceTest.OfflineRegressionReport {

  val repeats = Gen.range("repeats")(100, 500, 100)

  performance of "Selectors" in {

    measure method "buildIdCache" in {

      val html = {
        val in = Source.fromURL(classOf[SelectorsRegression].getResource("/B00796PHOC.html"))
        val text = in.mkString
        in.close()
        text
      }

      using(repeats) in { n =>
        val document = Jsoup.parse(html)
        for (i <- 0 until n) {
          val $ = Selectors.buildIdCache(document)
        }
      }
    }
  }
}
