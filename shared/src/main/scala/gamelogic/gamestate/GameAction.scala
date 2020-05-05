package gamelogic.gamestate

import gamelogic.gamestate.gameactions._
import gamelogic.gamestate.statetransformers.GameStateTransformer
import io.circe.{Decoder, Encoder, Json}

trait GameAction extends Ordered[GameAction] {

  val id: GameAction.Id

  /** Time at which the action occurred (in millis) */
  val time: Long

  /** Describes how this action affects a given GameState. */
  final def apply(gameState: GameState): GameState =
    createGameStateTransformer(gameState)(gameState)

  /**
    * Creates the [[gamelogic.gamestate.statetransformers.GameStateTransformer]] that will effectively affect the game.
    * If more than one building block must be used, you can compose them using their `++` method.
    */
  def createGameStateTransformer(gameState: GameState): GameStateTransformer

  def isLegal(gameState: GameState): Boolean

  final def compare(that: GameAction): Int = this.time compare that.time

  def changeId(newId: GameAction.Id): GameAction

}

object GameAction {

  type Id = Long

  import cats.syntax.functor._
  import io.circe.generic.auto._
  import io.circe.syntax._

  private def customEncode[A <: GameAction](a: A, name: String)(implicit encoder: Encoder[A]): Json =
    a.asJson.mapObject(_.add("action_name", Json.fromString(name)))

  implicit val encoder: Encoder[GameAction] = Encoder.instance {
    case x: AddPlayer           => customEncode(x, "AddPlayer")
    case x: DummyEntityMoves    => customEncode(x, "DummyEntityMoves")
    case x: EndGame             => customEncode(x, "EndGame")
    case x: EntityStartsCasting => customEncode(x, "EntityStartsCasting")
    case x: GameStart           => customEncode(x, "GameStart")
    case x: NewSimpleBullet     => customEncode(x, "NewSimpleBullet")
    case x: UpdateTimestamp     => customEncode(x, "UpdateTimestamp")
    case x: UseAbility          => customEncode(x, "UseAbility")
  }

  private def customDecoder[A <: GameAction](name: String)(implicit decoder: Decoder[A]): Decoder[GameAction] =
    decoder.validate(_.get[String]("action_name").contains(name), s"Not a $name instance").widen

  implicit val decoder: Decoder[GameAction] = List[Decoder[GameAction]](
    customDecoder[AddPlayer]("AddPlayer"),
    customDecoder[DummyEntityMoves]("DummyEntityMoves"),
    customDecoder[EndGame]("EndGame"),
    customDecoder[EntityStartsCasting]("EntityStartsCasting"),
    customDecoder[GameStart]("GameStart"),
    customDecoder[NewSimpleBullet]("NewSimpleBullet"),
    customDecoder[UpdateTimestamp]("UpdateTimestamp"),
    customDecoder[UseAbility]("UseAbility")
  ).reduceLeft(_ or _)

}