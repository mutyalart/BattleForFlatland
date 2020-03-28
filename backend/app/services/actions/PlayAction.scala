package services.actions

import play.api.mvc.Request
import zio.Tagged
import zio.{Has, Layer, UIO, ZLayer}

object PlayAction {

  def action[A, R[_]](
      request: R[A]
  )(
      implicit rTagged: Tagged[R[A]],
      aTagged: Tagged[A],
      ev: R[A] <:< Request[A]
  ): Layer[Nothing, Has[Action.Service[A]]] =
    ZLayer.succeed(new Action.Service[A] {
      def body: UIO[A] = UIO(request.body)

      def getFromSession(key: String): UIO[Option[String]] = UIO(request.session.get(key))
    })

}
