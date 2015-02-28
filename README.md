# ssoup: Scala CSS Selector DSL based on jsoup

## Install

```
"info.henix" % "ssoup" % "0.2"
```

## Example

```scala
import henix.ssoup.Selectors
import Selectors._

val document = Jsoup.parse(???)
val body = select1(document |>> "body") // BFS will find <body> more quickly than DFS
val nav = select(body > "div#page" > "div#nav").headOption

val $ = Selectors.buildIdCache(document)
val footer = $("footer")
```

## Supported structural selectors

* `>` : child
* `>>` : traverse descendants using DFS
* `|>>` : traverse descendants using BFS
* `~` : siblings
* `+` : immediate sibling

## API

### select(Iterator[Element]): Stream[Element]

Find all elements that match the selector.

### select1(Iterator[Element]): Element

Find the first element that matches the selector. This macro will generate friendly error message which include the selector.
