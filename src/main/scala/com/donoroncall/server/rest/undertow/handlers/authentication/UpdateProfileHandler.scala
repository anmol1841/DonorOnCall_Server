package com.donoroncall.server.rest.undertow.handlers.authentication
import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, EditProfileController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
 * Created by anmol on 11/3/16.
 */
class UpdateProfileHandler @Inject()(editProfileController:EditProfileController, sessionHandler: SessionHandler ) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val authToken = requestJson.getFields("token").head.asInstanceOf[JsString].value

        val userId = sessionHandler.getUserIdForSession(authToken)

        val name = requestJson.getFields("name").head.asInstanceOf[JsString].value
        val dob = requestJson.getFields("dob").head.asInstanceOf[JsString].value
        val bloodGroup = requestJson.getFields("blood_group").head.asInstanceOf[JsString].value
        val phoneNo = requestJson.getFields("phoneNo").head.asInstanceOf[JsString].value
        val email = requestJson.getFields("email").head.asInstanceOf[JsString].value

        val userUpdate = editProfileController.updateProfile(userId, name, dob, bloodGroup, phoneNo, email)




        if (userUpdate) {

          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "message" -> JsString("Profile Updated")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could Not update Profile")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could Not update Profile/Server Error")
          ).prettyPrint)
        }
      }
    }

  }
}
