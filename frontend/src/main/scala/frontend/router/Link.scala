package frontend.router

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.html
import urldsl.language.{PathSegment, PathSegmentWithQueryParams, QueryParameters}

object Link {

  def apply(to: PathSegment[Unit, _])(text: String): ReactiveHtmlElement[html.Span] =
    span(onClick --> (_ => Router.router.moveTo("/" + to.createPath())), text)

  def apply[Q](to: PathSegmentWithQueryParams[Unit, _, Q, _], q: Q)(text: String): ReactiveHtmlElement[html.Span] =
    span(onClick --> (_ => Router.router.moveTo("/" + to.createUrlString((), q))))

  def apply[Q](to: PathSegment[Unit, _], withParams: QueryParameters[Q, _], q: Q)(
      text: String
  ): ReactiveHtmlElement[html.Span] =
    apply(to ? withParams, q)(text)

}
