package utils

import errors.ErrorADT
import errors.ErrorADT.{MultipleErrors, MultipleErrorsMap}
import models.validators.{FieldsValidator, Validator}
import zio.{IO, ZIO}

package object ziohelpers {

  def failIfWith[E](mustFail: => Boolean, e: E): IO[E, Unit] = if (mustFail) ZIO.fail(e) else ZIO.succeed(())

  def validateOrFail[E <: ErrorADT, T](validator: Validator[T, E])(t: T): IO[ErrorADT, Unit] =
    validator(t) match {
      case Nil         => ZIO.succeed(())
      case head :: Nil => ZIO.fail(head)
      case errors      => ZIO.fail(MultipleErrors(errors))
    }

  def fieldsValidateOrFail[E <: ErrorADT, T](
      fieldsValidator: FieldsValidator[T, E]
  )(t: T): IO[MultipleErrorsMap, Unit] = {
    val errors = fieldsValidator.validate(t)
    if (errors.isEmpty) ZIO.succeed(())
    else ZIO.fail(MultipleErrorsMap(errors))
  }

}