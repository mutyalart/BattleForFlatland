package gamelogic.entities
import gamelogic.physics.Complex
import gamelogic.physics.shape.{Circle, Shape}

/**
  * A [[SimpleBulletBody]] goes forward at constant speed and damage the first thing it its.
  * @param range maximal distance this bullet can travel
  * @param ownerId the entity id of the entity responsible for creating this bullet.
  */
final case class SimpleBulletBody(
    id: Entity.Id,
    time: Long,
    pos: Complex,
    speed: Double,
    radius: Double,
    direction: Double,
    range: Double,
    ownerId: Entity.Id
) extends MovingBody {
  val shape: Shape     = new Circle(radius)
  val rotation: Double = 0.0 // doesn't matter, it's a disk
  val moving: Boolean  = true
}

object SimpleBulletBody {
  final def defaultRadius: Double = 4
}