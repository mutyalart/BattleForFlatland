package models.bff.ingame

import gamelogic.entities.Entity
import gamelogic.gamestate.GameAction
import gamelogic.physics.Complex
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}

sealed trait InGameWSProtocol

object InGameWSProtocol {

  /** Messages coming from the server to the client */
  sealed trait Incoming extends InGameWSProtocol

  /** Messages going to the server from the client */
  sealed trait Outgoing extends InGameWSProtocol

  /**
    * Received from time to time to keep the connection open. Probably useless given that game messages are constantly
    * sent.
    */
  case object HeartBeat extends Incoming

  /** A small Ping-Pong protocole is used before beginning the game to sync the clocks between client and server. */
  case class Ping(sendingTime: Long) extends Outgoing
  case class Pong(originalSendingTime: Long, midwayDistantTime: Long) extends Incoming

  /** Sent when the user is connected and the web socket is open */
  case class Ready(userId: String) extends Outgoing

  /** Sent when the user received their entity id, and all assets have been loaded. */
  case class ReadyToStart(userId: String) extends Outgoing

  /** Sent by a player to actually start the game at the very beginning. */
  case object LetsBegin extends Outgoing

  case class GameActionWrapper(gameActions: List[GameAction]) extends Outgoing
  case class RemoveActions(oldestTime: Long, idsOfActionsToRemove: List[GameAction.Id]) extends Incoming
  case class AddAndRemoveActions(
      actionsToAdd: List[GameAction],
      oldestTimeToRemove: Long,
      idsOfActionsToRemove: List[GameAction.Id]
  ) extends Incoming

  /** Received just before the beginning of the game so that the client knows what entity they control. */
  case class YourEntityIdIs(entityId: Entity.Id) extends Incoming

  /** Received before beginning the game to know where the boss will start. */
  case class StartingBossPosition(x: Double, y: Double) extends Incoming

  import io.circe.generic.extras.semiauto._
  implicit val genDevConfig: Configuration =
    Configuration.default.withDiscriminator("what_am_i_in_game")

  implicit def decoder: Decoder[InGameWSProtocol] = deriveConfiguredDecoder
  implicit def encoder: Encoder[InGameWSProtocol] = deriveConfiguredEncoder
}
