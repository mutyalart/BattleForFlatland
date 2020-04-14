package utils.websocket

import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.ownership.Owner
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.scalajs.dom
import org.scalajs.dom.raw.MessageEvent
import org.scalajs.dom.{Event, WebSocket}
import urldsl.language.QueryParameters.dummyErrorImpl._
import urldsl.language._
import zio.UIO

/**
  * Prepares a WebSocket to connect to the specified url.
  * The connection actually occurs when you run the `open` method.
  *
  * Messages coming from the server can be retrieved using the `$in` [[com.raquo.airstream.eventstream.EventStream]]
  * and sending messages to the server can be done by writing to the `outWriter` [[com.raquo.airstream.core.Observer]]
  */
final class JsonWebSocket[In, Out, P, Q] private (
    pathWithQueryParams: PathSegmentWithQueryParams[P, _, Q, _],
    p: P,
    q: Q
)(
    implicit decoder: Decoder[In],
    encoder: Encoder[Out]
) {

  private def url: String = "ws://" + dom.document.location.host + "/ws/" + pathWithQueryParams.createUrlString(p, q)

  private lazy val socket = new WebSocket(url)

  private val inBus: EventBus[In]           = new EventBus
  private val outBus: EventBus[Out]         = new EventBus
  private val closeBus: EventBus[Unit]      = new EventBus
  private val errorBus: EventBus[dom.Event] = new EventBus

  private def openWebSocketConnection(implicit owner: Owner) =
    for {
      webSocket <- UIO(socket)
      _ <- UIO {
        webSocket.onmessage = (event: MessageEvent) => {
          decode[In](event.data.asInstanceOf[String]) match {
            case Right(in) => inBus.writer.onNext(in)
            case Left(error) =>
              dom.console.log("data", event.data)
              dom.console.error(error)
          }
        }
      }
      _ <- UIO {
        outBus.events.map(encoder.apply).map(_.noSpaces).foreach(webSocket.send)
        outBus.events.map(encoder.apply).map(_.noSpaces).foreach(println)
      }
      _ <- UIO {
        webSocket.onerror = (event: Event) => {
          if (scala.scalajs.LinkingInfo.developmentMode) {
            dom.console.error(event)
          }
          errorBus.writer.onNext(event)
        }
      }
    } yield ()

  def open()(implicit owner: Owner): Unit =
    zio.Runtime.default.unsafeRunToFuture(openWebSocketConnection)

  def close(): Unit = {
    socket.close()
    closeBus.writer.onNext(())
  }

  val $in: EventStream[In]       = inBus.events
  val outWriter: WriteBus[Out]   = outBus.writer
  val $closed: EventStream[Unit] = closeBus.events
  val $error: EventStream[Event] = errorBus.events

}

object JsonWebSocket {

  def apply[In, Out](path: PathSegment[Unit, _])(
      implicit decoder: Decoder[In],
      encoder: Encoder[Out]
  ): JsonWebSocket[In, Out, Unit, Unit] = new JsonWebSocket(path ? empty, (), ())

  def apply[In, Out, Q](path: PathSegment[Unit, _], query: QueryParameters[Q, _], q: Q)(
      implicit decoder: Decoder[In],
      encoder: Encoder[Out]
  ): JsonWebSocket[In, Out, Unit, Q] = new JsonWebSocket(path ? query, (), q)

}
