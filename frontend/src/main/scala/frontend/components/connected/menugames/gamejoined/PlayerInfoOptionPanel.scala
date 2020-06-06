package frontend.components.connected.menugames.gamejoined

import com.raquo.airstream.core.Observer
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.components.Component
import frontend.components.utils.{ColorPickerWrapper, ToggleButton}
import frontend.components.utils.tailwind.{primaryColour, primaryColourDark}
import models.bff.outofgame.PlayerClasses
import models.bff.outofgame.gameconfig.PlayerStatus.{NotReady, Ready}
import models.bff.outofgame.gameconfig.{PlayerInfo, PlayerStatus}
import org.scalajs.dom.html
import org.scalajs.dom.html.{Element, Select}
import utils.misc.{Colour, RGBAColour, RGBColour}
import frontend.components.utils.laminarutils.reactChildInDiv
import frontend.components.utils.popper.{PlacementOption, PopperElement}

final class PlayerInfoOptionPanel private (initialPlayerInfo: PlayerInfo, playerInfoWriter: Observer[PlayerInfo])
    extends Component[html.Element] {

  def changeReadyState(ready: PlayerStatus): PlayerInfo => PlayerInfo   = _.copy(status       = ready)
  def changeColour(colour: RGBColour): PlayerInfo => PlayerInfo         = _.copy(playerColour = colour)
  def changeClass(playerClass: PlayerClasses): PlayerInfo => PlayerInfo = _.copy(playerClass  = playerClass)

  val changerBus: EventBus[PlayerInfo => PlayerInfo] = new EventBus
  val readyStateWriter: Observer[PlayerStatus]       = changerBus.writer.contramap(changeReadyState)
  val playerClassWriter: Observer[PlayerClasses]     = changerBus.writer.contramap(changeClass)
  val colourWriter: Observer[RGBAColour]             = changerBus.writer.contramap(changeColour).contramap(_.removeAlpha)

  val $playerInfo: Signal[PlayerInfo] = changerBus.events.fold(initialPlayerInfo) { (info, changer) =>
    changer(info)
  }

  val classSelector: ReactiveHtmlElement[Select] = select(
    PlayerClasses.allChoices.map(_.toString).map(cls => option(value := cls, cls)),
    inContext(
      elem =>
        onChange.mapTo(elem.ref.value).map(PlayerClasses.playerClassByName).collect {
          case Some(value) => value
        } --> playerClassWriter
    ),
    onMountSet(_ => value := initialPlayerInfo.playerClass.toString)
  )

  val pickerPositionBus: EventBus[(Double, Double)] = new EventBus
  val $maybePickerPosition: Signal[Option[(Double, Double)]] =
    pickerPositionBus.events.fold(Option.empty[(Double, Double)]) {
      case (None, (x, y)) => Some((x, y))
      case (Some(_), _)   => None
    }
  val colourSelector: ReactiveHtmlElement[html.Div] = div(
    "Chose a color: ",
    div(
      height := "30px",
      width := "50px",
      backgroundColor <-- $playerInfo.map(_.playerColour.rgb),
      onClick.map(event => (event.pageX, event.pageY)) --> pickerPositionBus
    ),
    child <-- $maybePickerPosition.map(_.map {
      case (x, y) =>
        div(
          zIndex := "2",
          position := "absolute",
          left := s"${x + 10}px",
          top := s"${y + 10}px",
          reactChildInDiv(ColorPickerWrapper(colourWriter, initialPlayerInfo.playerColour))
        )
    }).map(_.getOrElse(emptyNode))
  )

  val element: ReactiveHtmlElement[Element] =
    section(
      h2(
        className := "text-2xl",
        className := s"text-$primaryColour-$primaryColourDark",
        "Player Options"
      ),
      "Ready: ",
      ToggleButton(readyStateWriter.contramap(if (_) Ready else NotReady), initialPlayerInfo.isReady),
      div(
        "Choose a class:",
        classSelector
      ),
      colourSelector,
      $playerInfo --> playerInfoWriter
    )
}

object PlayerInfoOptionPanel {
  def apply(initialPlayerInfo: PlayerInfo, playerInfoWriter: Observer[PlayerInfo]): PlayerInfoOptionPanel =
    new PlayerInfoOptionPanel(initialPlayerInfo, playerInfoWriter)
}
