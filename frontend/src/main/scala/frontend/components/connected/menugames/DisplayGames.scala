package frontend.components.connected.menugames

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.components.picto.SmallKey
import frontend.components.utils.tailwind._
import frontend.components.utils.tailwind.components.Table._
import frontend.components.{Component, ModalWindow}
import models.bff.outofgame.MenuGame
import org.scalajs.dom.html
import org.scalajs.dom.html.TableRow

final class DisplayGames private ($games: EventStream[List[MenuGame]], showNewGameWriter: Observer[Unit])
    extends Component[html.Element] {

  /** Write a Some(game) for opening the join game panel, or None to close it. */
  val showJoinGameModalBus: EventBus[Option[MenuGame]] = new EventBus
  val closeJoinGameWriter: Observer[Unit]              = showJoinGameModalBus.writer.contramap(_ => None)
  val openJoinGameWriter: Observer[MenuGame]           = showJoinGameModalBus.writer.contramap(Some(_))
  val $openJoinGame: EventStream[Option[MenuGame]]     = showJoinGameModalBus.events

  def renderGameRow(gameId: String, game: MenuGame, gameStream: EventStream[MenuGame]): ReactiveHtmlElement[TableRow] =
    tr(
      clickableRow,
      td(tableData, child.text <-- gameStream.map(_.gameName)),
      td(tableData, child.text <-- gameStream.map(_.gameCreator.userName)),
      td(
        tableData,
        child <-- gameStream.map(_.maybeHashedPassword.map(_ => SmallKey().element).getOrElse(emptyNode))
      ),
      onClick.mapTo(game) --> openJoinGameWriter
    )

  val element: ReactiveHtmlElement[html.Element] = section(
    mainContentContainer,
    div(
      mainContent,
      className := "bg-gray-200",
      div(
        className := "flex items-start justify-between border-b-2",
        h1(pad(4), className := "text-xl", "Join a game"),
        button(
          btn,
          primaryButton,
          "New Game",
          onClick.mapTo(()) --> showNewGameWriter
        )
      ),
      div(
        child <-- $games.map {
          case Nil =>
            p(
              pad(2),
              textPrimaryColour,
              "There is currently no game. You can start a new one ",
              span(
                "here",
                textPrimaryColourLight,
                cursorPointer,
                className := s"hover:text-$primaryColour-$primaryColourDark",
                onClick.mapTo(()) --> showNewGameWriter
              ),
              "."
            )
          case _ =>
            table(
              className := "bg-white w-full",
              textPrimaryColour,
              thead(
                tr(
                  th(tableHeader, "Game name"),
                  th(tableHeader, "Created by"),
                  th(tableHeader, "")
                )
              ),
              tbody(
                children <-- $games.split(_.gameId)(renderGameRow)
              )
            )
        }
      )
    ),
    child <-- $openJoinGame.map {
      case Some(game) =>
        ModalWindow(JoinGameModal(game, closeJoinGameWriter), closeJoinGameWriter)
      case None => emptyNode
    }
  )

}

object DisplayGames {
  def apply($games: EventStream[List[MenuGame]], showNewGameWriter: Observer[Unit]) =
    new DisplayGames($games, showNewGameWriter)
}
