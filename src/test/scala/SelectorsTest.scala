package henix.ssoup

import org.scalatest.FunSuite

class SelectorsTest extends FunSuite {

  def is_id(q: String) = test(q + " is id") {
    val ev = QueryParser.parse(q)
    assert(Selectors.isIdEvaluator(ev))
  }

  def is_not_id(q: String) = test(q + " is not id") {
    val ev = QueryParser.parse(q)
    assert(!Selectors.isIdEvaluator(ev))
  }

  is_id("#id")
  is_id("input#merchantID")
  is_not_id("div.rbbSection")
}
