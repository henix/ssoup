package henix.ssoup

import java.util

import org.jsoup.nodes.{DataNode, Element, Node}
import org.jsoup.select.Evaluator

import scala.collection.JavaConverters.{asScalaBufferConverter, asScalaIteratorConverter}
import scala.language.experimental.macros
import scala.language.implicitConversions

object QueryParser {
  private val queryParserClass = Class.forName("org.jsoup.select.QueryParser")
  private val queryParser = queryParserClass.getDeclaredMethod("parse", classOf[String])
  queryParser.setAccessible(true)
  private val cachedParse = Memoize1((q: String) => queryParser.invoke(null, q).asInstanceOf[Evaluator])

  def parse(cssquery: String): Evaluator = cachedParse(cssquery)
}

object Selectors {

  /**
   * @param evaluator must not contain structural selector
   */
  private def filterByEvaluator(evaluator: Evaluator, eles: Iterator[Element]): Iterator[Element] = {
    eles.filter(e => evaluator.matches(e, e))
  }

  implicit def cssquery(css: String): Evaluator = QueryParser.parse(css)

  def getScriptText(script: Element): String = script.childNodes().asScala.collect({ case d: DataNode => d }).map(_.getWholeData).mkString

  /**
   * 不能用常规的 :contains() 因为 DataNode 不是文本
   */
  class ScriptContains(str: String) extends Evaluator {
    override def matches(root: Element, element: Element): Boolean = element.tagName() == "script" && getScriptText(element).contains(str)
    override lazy val toString = s"script:contains($str)"
  }

  def scriptContains(str: String) = new ScriptContains(str)

  class ScriptThat(predicate: String => Boolean) extends Evaluator {
    override def matches(root: Element, element: Element): Boolean = element.tagName() == "script" && predicate(getScriptText(element))
  }

  def scriptThat(p: String => Boolean) = new ScriptThat(p)

  implicit class ElementsOps(elements: Iterator[Element]) {

    def >(eval: Evaluator): Iterator[Element] = elements.flatMap(_ > eval)

    def >>(eval: Evaluator): Iterator[Element] = elements.flatMap(_ >> eval)

    def ~(eval: Evaluator): Iterator[Element] = elements.flatMap(_ ~ eval)

    def +(eval: Evaluator): Iterator[Element] = elements.flatMap(_ + eval)

    /**
     * 删除时必须先转成 List，拿到全部元素后才能删除。如果一边遍历一边删除，会导致 ConcurrentModificationException
     */
    def remove(): Unit = elements.toList.foreach(_.remove())
  }

  /**
   * 注意[运算符的优先级](http://stackoverflow.com/questions/2922347/operator-precedence-in-scala)
   */
  implicit class ElementOps(element: Element) {

    def >(eval: Evaluator): Iterator[Element] = filterByEvaluator(eval, getChilds(element))

    def >>(eval: Evaluator): Iterator[Element] = filterByEvaluator(eval, dfsTraverse(element))

    def ~(eval: Evaluator): Iterator[Element] = filterByEvaluator(eval, getSiblings(element))

    def +(eval: Evaluator): Iterator[Element] = filterByEvaluator(eval, getSiblings(element).take(1))

    def |>>(eval: Evaluator): Iterator[Element] = filterByEvaluator(eval, new BFSElementIterator(element))
  }

  /**
   * 所有选择器都应该包在 select 中，因为 Iterator 是有状态的，而且只能使用一次
   */
  def select(elements: Iterator[Element]): Stream[Element] = elements.toStream

  def select1(elements: Iterator[Element]): Element = macro SelectorMacros.select1_impl

  def getChilds(el: Element): Iterator[Element] = el.childNodes().iterator().asScala.collect({ case e: Element => e })

  /**
   * 取 e 的所有 nextSibling
   *
   * 参考 Element#nextSibling 实现
   */
  def getSiblingNodes(e: Node): Iterator[Node] = {
    val parent = e.parent()
    if (parent ne null) {
      val i = e.siblingIndex()
      val childs = parent.childNodes()
      if (i + 1 < childs.size()) {
        childs.listIterator(i + 1).asScala
      } else {
        Iterator.empty
      }
    } else {
      Iterator.empty
    }
  }

  def getSiblings(e: Element): Iterator[Element] = getSiblingNodes(e).collect({ case e: Element => e })

  def dfsTraverse(e: Element): Iterator[Element] = Iterator.single(e) ++ getChilds(e).flatMap(dfsTraverse)

  /**
   * 以 BFS 遍历 DOM
   *
   * 1. 保存 Iterator ，以 lazy 的方式提高性能
   * 2. 考虑到 scala.collection.immutable.Queue 的性能可能不如 ArrayDeque ，故采用 ArrayDeque
   */
  class BFSElementIterator(root: Element) extends Iterator[Element] {

    private var queue: util.ArrayDeque[Iterator[Element]] = new util.ArrayDeque[Iterator[Element]]()
    queue.addLast(Iterator(root))

    /**
     * 使用 invariant: 开头部分没有 empty 的 iterator
     */
    override def hasNext: Boolean = !queue.isEmpty

    /**
     * 维护 invariant: queue 中不能有 empty 的 iterator
     */
    override def next(): Element = {
      val head = queue.getFirst
      val res = head.next()
      if (!head.hasNext) {
        queue.removeFirst()
      }
      val childs = res.childNodes().iterator().asScala.collect({ case e: Element => e })
      if (childs.hasNext) {
        queue.addLast(childs)
      }
      res
    }
  }

  def buildIdCache(el: Element): Map[String, Element] = dfsTraverse(el).collect({ case e if e.hasAttr("id") => e }).map(e => e.id() -> e).toMap
}
