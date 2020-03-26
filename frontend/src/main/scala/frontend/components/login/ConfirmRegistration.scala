package frontend.components.login

import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.components.Component
import org.scalajs.dom.html
import org.scalajs.dom.html.Div
import com.raquo.laminar.api.L._
import errors.ErrorADT
import frontend.router.{Link, RouteDefinitions}
import services.http._
import utils.laminarzio.Implicits._
import programs.frontend.login.confirmRegistrationCall

final class ConfirmRegistration private (registrationKey: String) extends Component[html.Div] {

  val $confirmRegistration: EventStream[Either[ErrorADT, Int]] =
    EventStream.fromZIOEffect(confirmRegistrationCall(registrationKey).provideLayer(FrontendHttpClient.live))

  val $confirmSuccess: EventStream[Int]      = $confirmRegistration.collect { case Right(code) => code }
  val $confirmFailure: EventStream[ErrorADT] = $confirmRegistration.collect { case Left(error) => error }

  val element: ReactiveHtmlElement[Div] = div(
    className := "ConfirmRegistration",
    h1("Confirm registration"),
    p(
      "Registration key: ",
      registrationKey
    ),
    child <-- $confirmSuccess.map { _ =>
      div(
        h2("Registration succeeded!"),
        p(
          "You can now connect to Battle for flatland.",
          br(),
          Link(RouteDefinitions.loginRoute)("Login")
        )
      )
    },
    child <-- $confirmFailure.map { error =>
      div(
        h2("Fatal error"),
        p(s"Error code: $error")
      )
    }
  )

}

object ConfirmRegistration {
  def apply(registrationKey: String): ConfirmRegistration = new ConfirmRegistration(registrationKey)

}
