package frontend.components.connected.menugames.gamejoined

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.components.Component
import frontend.components.utils.tailwind.{primaryColour, primaryColourDark}
import gamelogic.entities.boss.BossEntity
import models.bff.gameantichamber.WebSocketProtocol
import models.bff.outofgame.MenuGameWithPlayers
import org.scalajs.dom.html

final class GameOptionPanel private (initialGameInfo: MenuGameWithPlayers, socketOutWriter: Observer[WebSocketProtocol])
    extends Component[html.Element] {

  val element: ReactiveHtmlElement[html.Element] = section(
    h2(
      className := "text-2xl",
      className := s"text-$primaryColour-$primaryColourDark",
      "Game Options"
    ),
    select(
      BossEntity.allBossesNames.map(name => option(value := name, name)),
      onMountSet(_ => {
        initialGameInfo.game.gameConfiguration.maybeBossName
          .fold(BossEntity.allBossesNames.headOption)(_ => None) // do nothing if boss is already defined
          .foreach { name =>
            socketOutWriter.onNext(WebSocketProtocol.UpdateBossName(name))
          }
        value := initialGameInfo.game.gameConfiguration.maybeBossName.getOrElse(BossEntity.allBossesNames.head)
      }),
      inContext(elem => onChange.mapTo(elem.ref.value).map(WebSocketProtocol.UpdateBossName) --> socketOutWriter)
    )
  )

}

object GameOptionPanel {
  def apply(initialGameInfo: MenuGameWithPlayers, socketOutWriter: Observer[WebSocketProtocol]): GameOptionPanel =
    new GameOptionPanel(initialGameInfo, socketOutWriter)
}
