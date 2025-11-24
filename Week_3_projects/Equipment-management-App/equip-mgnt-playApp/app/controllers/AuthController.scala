package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject._
import services.AuthService
import models.User
import security.JwtUtil
import scala.concurrent.{ExecutionContext, Future}

case class RegisterRequest(username: String, password: String, role: String)
case class LoginRequest(username: String, password: String)

object AuthJsonFormats {
  implicit val registerFormat = Json.format[RegisterRequest]
  implicit val loginFormat = Json.format[LoginRequest]
  implicit val userFormat = Json.format[User]
}

@Singleton
class AuthController @Inject()(
                                cc: ControllerComponents,
                                authService: AuthService
                              )(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  import AuthJsonFormats._

  // REGISTER
  def register: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[RegisterRequest].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      req =>
        authService.register(req.username, req.password, req.role).map { id =>
          Ok(Json.obj("status" -> "success", "userId" -> id))
        }
    )
  }

  // LOGIN
  def login: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[LoginRequest].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      req =>
        authService.login(req.username, req.password).map {
          case Some(user) =>
            val token = JwtUtil.generateToken(user.username)

            Ok(Json.obj(
              "status"   -> "success",
              "token"    -> token,
              "username" -> user.username,
              "role"     -> user.role
            ))

          case None =>
            Unauthorized(Json.obj(
              "status"  -> "error",
              "message" -> "Invalid credentials"
            ))
        }
    )
  }
}
