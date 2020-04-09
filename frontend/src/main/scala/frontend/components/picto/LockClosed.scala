package frontend.components.picto

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.components.LifecycleComponent
import org.scalajs.dom.html

final class LockClosed private () extends LifecycleComponent[html.Span] {

  val elem: ReactiveHtmlElement[html.Span] = span(
//    svg(
//      xmlns := "http://www.w3.org/2000/svg",
//      viewBox := "0 0 24 24",
//      width := "24",
//      height := "24",
//      path(
//        d := "M7 10V7a5 5 0 1 1 10 0v3h2a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-8c0-1.1.9-2 2-2h2zm2 0h6V7a3 3 0 0 0-6 0v3zm-4 2v8h14v-8H5zm7 2a1 1 0 0 1 1 1v2a1 1 0 0 1-2 0v-2a1 1 0 0 1 1-1z"
//      )
//    )
  )

  override def componentDidMount(): Unit =
    elem.ref.innerHTML =
      """
        |<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
        |  <path class="heroicon-ui" d="M7 10V7a5 5 0 1 1 10 0v3h2a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-8c0-1.1.9-2 2-2h2zm2 0h6V7a3 3 0 0 0-6 0v3zm-4 2v8h14v-8H5zm7 2a1 1 0 0 1 1 1v2a1 1 0 0 1-2 0v-2a1 1 0 0 1 1-1z"/>
        |</svg>
        |""".stripMargin

}

object LockClosed {
  def apply() = new LockClosed
}