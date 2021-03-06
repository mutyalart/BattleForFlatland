package assets

import gamelogic.abilities.Ability
import gamelogic.buffs.Buff

import scala.collection.mutable
import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.util.Try

trait Asset extends js.Object

object Asset {

  final class AssetNotProperlyDefined(asset: Asset)
      extends Exception(
        s"The asset `${asset.toString}` does not seem to work. Did you forget to restart the Dev server? Or Perhaps" +
          s"you forgot to add the asset in resource path? A typo in the filepath could also cause that."
      )

  object ingame {
    object gui {
      object abilities {
        @js.native @JSImport("resources/assets/in-game/gui/abilities/ability-overlay.png", JSImport.Default)
        object abilityOverlay extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/abilities/hexagon-flash-heal.png", JSImport.Default)
        object hexagonFlashHeal extends Asset
        @js.native @JSImport("resources/assets/in-game/gui/abilities/hexagon-hot.png", JSImport.Default)
        object hexagonHot extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/abilities/square-hammer-hit.png", JSImport.Default)
        object squareHammerHit extends Asset
        @js.native @JSImport("resources/assets/in-game/gui/abilities/square-taunt.png", JSImport.Default)
        object squareTaunt extends Asset
        @js.native @JSImport("resources/assets/in-game/gui/abilities/square-enrage.png", JSImport.Default)
        object squareEnrage extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/abilities/triangle-direct-hit.png", JSImport.Default)
        object triangleDirectHit extends Asset
        @js.native @JSImport("resources/assets/in-game/gui/abilities/triangle-upgrade-direct-hit.png", JSImport.Default)
        object triangleUpgradeDirectHit extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/abilities/pentagon-bullet.png", JSImport.Default)
        object pentagonBullet extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/abilities/boss101-big-dot.png", JSImport.Default)
        object boss101BigDot extends Asset

      }

      object bars {
        @js.native @JSImport("resources/assets/in-game/gui/bars/LiteStep.png", JSImport.Default)
        object liteStepBar extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/bars/Minimalist.png", JSImport.Default)
        object minimalistBar extends Asset

        @js.native @JSImport("resources/assets/in-game/gui/bars/Xeon.png", JSImport.Default)
        object xeonBar extends Asset
      }

      object `default-abilities` {
        @js.native @JSImport(
          "resources/assets/in-game/gui/default-abilities/players/square-shield.png",
          JSImport.Default
        )
        object squareShield extends Asset

        @js.native @JSImport(
          "resources/assets/in-game/gui/default-abilities/players/rage-filler.png",
          JSImport.Default
        )
        object rageFiller extends Asset

        @js.native @JSImport(
          "resources/assets/in-game/gui/default-abilities/players/energy-filler.png",
          JSImport.Default
        )
        object energyFiller extends Asset

        @js.native @JSImport(
          "resources/assets/in-game/gui/default-abilities/players/mana-filler.png",
          JSImport.Default
        )
        object manaFiller extends Asset
      }
    }
  }

  implicit def assetAsString(asset: Asset): String =
    Try(asset.asInstanceOf[String]).fold(_ => throw new AssetNotProperlyDefined(asset), identity[String])

  // Touching all assets so that there are loaded
  ScalaLogo
  ingame.gui.bars.liteStepBar
  ingame.gui.bars.minimalistBar
  ingame.gui.bars.xeonBar
  ingame.gui.abilities.abilityOverlay
  ingame.gui.abilities.hexagonFlashHeal
  ingame.gui.abilities.hexagonHot
  ingame.gui.abilities.squareHammerHit
  ingame.gui.abilities.squareTaunt
  ingame.gui.abilities.squareEnrage
  ingame.gui.abilities.triangleDirectHit
  ingame.gui.abilities.triangleUpgradeDirectHit
  ingame.gui.abilities.pentagonBullet
  ingame.gui.abilities.boss101BigDot
  ingame.gui.`default-abilities`.squareShield
  ingame.gui.`default-abilities`.rageFiller
  ingame.gui.`default-abilities`.energyFiller
  ingame.gui.`default-abilities`.manaFiller

  final val abilityAssetMap: Map[Ability.AbilityId, Asset] = Map(
    Ability.hexagonHexagonHotId -> ingame.gui.abilities.hexagonHot,
    Ability.hexagonFlashHealId -> ingame.gui.abilities.hexagonFlashHeal,
    Ability.squareHammerHit -> ingame.gui.abilities.squareHammerHit,
    Ability.squareTauntId -> ingame.gui.abilities.squareTaunt,
    Ability.squareEnrageId -> ingame.gui.abilities.squareEnrage,
    Ability.triangleDirectHit -> ingame.gui.abilities.triangleDirectHit,
    Ability.triangleUpgradeDirectHit -> ingame.gui.abilities.triangleUpgradeDirectHit,
    Ability.pentagonPentagonBullet -> ingame.gui.abilities.pentagonBullet
  )

  final val buffAssetMap: Map[Buff.ResourceIdentifier, Asset] = Map(
    Buff.hexagonHotIdentifier -> ingame.gui.abilities.hexagonHot,
    Buff.boss101BigDotIdentifier -> ingame.gui.abilities.boss101BigDot,
    Buff.squareDefaultShield -> ingame.gui.`default-abilities`.squareShield,
    Buff.rageFiller -> ingame.gui.`default-abilities`.rageFiller,
    Buff.energyFiller -> ingame.gui.`default-abilities`.energyFiller,
    Buff.manaFiller -> ingame.gui.`default-abilities`.manaFiller,
    Buff.triangleUpgradeDirectHit -> ingame.gui.abilities.triangleUpgradeDirectHit,
    Buff.squareEnrage -> ingame.gui.abilities.squareEnrage
  )

}
