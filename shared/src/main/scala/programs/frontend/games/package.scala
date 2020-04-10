package programs.frontend

import errors.ErrorADT
import io.circe.generic.auto._
import models.bff.Routes._
import models.bff.outofgame.{MenuGame, MenuGameWithPlayers}
import models.common.PasswordWrapper
import models.syntax.Pointed
import models.users.RouteDefinitions._
import services.http._
import zio.{UIO, _}
import zio.clock._
import zio.stream._
import services.routing._
import services.logging._

import scala.concurrent.duration._
import utils.ziohelpers.unsuccessfulStatusCode

package object games {

  val streamExpl: ZStream[Clock, Nothing, Int] =
    ZStream
      .fromSchedule(Schedule.spaced(zio.duration.Duration.fromScala(2.seconds)))
      .tap(x => UIO(println(x)))

  val downloadGames: ZIO[HttpClient, ErrorADT, List[MenuGame]] =
    get[List[MenuGame]](allGames).refineOrDie(ErrorADT.onlyErrorADT)

  val loadGames: ZStream[HttpClient with Clock, Nothing, Either[ErrorADT, List[MenuGame]]] = ZStream
    .fromSchedule(Schedule.spaced(zio.duration.Duration.fromScala(5.seconds)))
    .flatMap(_ => ZStream.fromEffect(downloadGames.either))

  def createNewGame(game: MenuGame): ZIO[HttpClient, Throwable, String] = post[MenuGame, String](newMenuGame, game)

  def joinGameProgram(game: MenuGame, maybePassword: PasswordWrapper): ZIO[Routing with HttpClient, Throwable, Int] =
    for {
      statusCode <- postIgnore(joinGame, joinGameParam, maybePassword)(game.gameId)
      _ <- unsuccessfulStatusCode(statusCode)
      _ <- moveTo(gameJoined ? gameIdParam)(game.gameId)
    } yield statusCode

  val amIAmPlayingSomewhere: ZIO[Routing with HttpClient, Throwable, Unit] = for {
    maybeGameId <- get[Option[String]](amIPlaying)
    _ <- maybeGameId match {
      case Some(gameId) => moveTo(gameJoined ? gameIdParam)(gameId)
      case None         => UIO(())
    }
  } yield ()

  /**
    * Fetch game information.
    * If there is an error in the process, currently we assume that any error means that the client should go back to
    * the home page.
    * // todo: store the error using the storage service
    * // todo: refinement of behaviour depending on the actual error.
    * @return the game information wrapped into an Option if it succeed, None otherwise.
    */
  def fetchGameInfo(gameId: String): URIO[Routing with Logging with HttpClient, Option[MenuGameWithPlayers]] =
    get[String, MenuGameWithPlayers](gameInfo, gameIdParam)(gameId)
      .flatMapError(
        error =>
          for {
            _ <- log.error(error.toString).ignore
            _ <- moveTo(homeRoute)
          } yield ()
      )
      .option

}
