package errors

import errors.HTTPErrorType._
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}

/**
  * Parent of all errors that are handled in both the frontend and the backend.
  *
  * This ADT is meant to give more meaningful errors in ZIO error channel, by looking
  * at smaller smallest parent class when resolving errors.
  *
  */
sealed trait ErrorADT extends Throwable {
  def httpErrorType: HTTPErrorType
}

object ErrorADT {

  case class MultipleErrors(errors: List[ErrorADT]) extends ErrorADT {
    def httpErrorType: HTTPErrorType = errors.headOption.map(_.httpErrorType).getOrElse(Internal)
  }

  case class MultipleErrorsMap(errors: Map[String, List[ErrorADT]]) extends ErrorADT {
    def httpErrorType: HTTPErrorType =
      errors.toList.headOption.flatMap(_._2.headOption).map(_.httpErrorType).getOrElse(Internal)
  }

  /** Errors that can be thrown in the backend. */
  sealed trait BackendError extends ErrorADT

  sealed trait DatabaseError extends BackendError
  case class UserExists(userName: String) extends Exception(userName) with DatabaseError {
    def httpErrorType: HTTPErrorType = BadRequest
  }
  case class UserDoesNotExist(userName: String) extends Exception(userName) with DatabaseError {
    def httpErrorType: HTTPErrorType = BadRequest
  }
  case object CantDeleteTheBoss extends DatabaseError {
    def httpErrorType: HTTPErrorType = Forbidden
  }

  sealed trait AuthenticationError extends ErrorADT
  case object YouAreUnauthorized extends AuthenticationError {
    def httpErrorType: HTTPErrorType = Unauthorized
  }
  case object ForbiddenForYou extends AuthenticationError {
    def httpErrorType: HTTPErrorType = Forbidden
  }
  case object IncorrectPassword extends AuthenticationError {
    override def httpErrorType: HTTPErrorType = BadRequest
  }

  private case class Wrapper(error: ErrorADT, name: String)

  sealed trait FrontendError extends ErrorADT
  case object PasswordsMismatch extends FrontendError {
    def httpErrorType: HTTPErrorType = BadRequest
  }

  sealed trait ValidatorError extends ErrorADT {
    def httpErrorType: HTTPErrorType = BadRequest
  }
  sealed trait NumericValidatorError extends ValidatorError
  case class NonZero(value: String) extends NumericValidatorError
  case class NotBiggerThan(value: String, threshold: String) extends NumericValidatorError
  case class NotSmallerThan(value: String, threshold: String) extends NumericValidatorError
  case class Negative(value: String) extends NumericValidatorError
  sealed trait StringValidatorError extends ValidatorError
  case object StringIsEmpty extends StringValidatorError
  case class StringIsTooShort(str: String, threshold: Int) extends StringValidatorError
  case class ContainsNonLowercaseAlphabet(str: String) extends StringValidatorError
  case class ShouldContain(substr: String, str: String) extends StringValidatorError
  case class ShouldNotContain(substr: String, str: String) extends StringValidatorError

  import io.circe.generic.extras.semiauto._
  implicit val genDevConfig: Configuration =
    Configuration.default.withDiscriminator("what_am_i")

  implicit def decoder: Decoder[ErrorADT] = deriveConfiguredDecoder
  implicit def encoder: Encoder[ErrorADT] = deriveConfiguredEncoder

}
