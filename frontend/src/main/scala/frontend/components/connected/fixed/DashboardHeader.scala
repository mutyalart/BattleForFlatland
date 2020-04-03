package frontend.components.connected.fixed

import com.raquo.airstream.eventstream.EventStream
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.components.Component
import frontend.components.utils.Logout
import org.scalajs.dom.html

final class DashboardHeader private ($userName: EventStream[String]) extends Component[html.Element] {
  val element: ReactiveHtmlElement[html.Element] = header(
    child <-- $userName.startWith("...").map(span(_)),
    h1("Battle for Flatland"),
    span(className := "primary", Logout())
  )
}

object DashboardHeader {
  def apply($userName: EventStream[String]) = new DashboardHeader($userName)
}