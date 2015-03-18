package henix.ssoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.scalatest.{Matchers, FunSuite}

import scala.io.Source

class SelectorsTest extends FunSuite with Matchers {

  val doc = {
    val in = Source.fromURL(classOf[SelectorsTest].getResource("/B00796PHOC.html"))
    val text = in.mkString
    in.close()
    Jsoup.parse(text)
  }

  def dfsTraverseRefImpl(e: Element): Iterator[Element] = Iterator.single(e) ++ Selectors.getChilds(e).flatMap(dfsTraverseRefImpl)

  test("dfsTraverse should traverse elements in exactly the same order of ref impl") {
    val refList = dfsTraverseRefImpl(doc).toArray
    val list = Selectors.dfsTraverse(doc).toArray

    list.length should be (refList.length)
    for (i <- 0 until list.length) {
      list(i) should be theSameInstanceAs refList(i)
    }
  }
}
