package henix.ssoup

import org.jsoup.nodes.Element

import scala.reflect.macros.blackbox.Context

object SelectorMacros {

  private implicit class MyStringOps(s: String) {
    def removeStart(remove: String): String = if (s.startsWith(remove)) s.substring(remove.length) else s
    def removeEnd(remove: String): String = if (s.endsWith(remove)) s.substring(0, s.length - remove.length) else s
  }

  /**
   * Provide better error message
   */
  def select1_impl(c: Context)(elements: c.Expr[Iterator[Element]]): c.Expr[Element] = {
    import c.universe._
    val text = show(elements).replaceAll("henix\\.ssoup\\.Selectors\\.(?:ElementsOps|ElementOps|cssquery)", "").removeStart("Expr[Nothing](").removeEnd(")")
    c.Expr[Element](
      q"""
          select($elements).headOption.getOrElse(throw new java.util.NoSuchElementException($text))
      """
    )
  }
}
