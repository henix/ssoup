# ssoup: Scala CSS Selector DSL based on jsoup

## Install

```
"info.henix" %% "ssoup" % "0.4.1"
```

* DO NOT use v0.3

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

## Performance Consideration

In contrast to many selectors implementations, ssoup implement selectors in a more direct way. This means:

* `>` will iterate element's children
* `~` will iterate element's siblings
* `>>` and `|>>` will traverse element's all descendants

So you should avoid using more than one `>>` or `|>>` in a select / select1 call.
