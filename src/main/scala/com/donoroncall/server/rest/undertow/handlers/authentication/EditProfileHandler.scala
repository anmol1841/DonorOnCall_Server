package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.EditProfileController
import com.donoroncall.server.rest.controllers.authentication.SessionHandler
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
 * Created by anmol on 11/3/16.
 */
class EditProfileHandler @Inject()(editProfileController:EditProfileController, sessionHandler: SessionHandler ) extends HttpHandler {
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


        val bloodGroup = editProfileController.getBloodGroup(userId)

        val dob = editProfileController.getDob(userId)

        val name = editProfileController.getName(userId)

        val email = editProfileController.getEmail(userId)

         val phoneNo = editProfileController.getPhone(userId)

        if (userId != null) {
          // to verify if this is the correct way
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "userId" -> JsNumber(userId),
            "bloodGroup" -> JsString(bloodGroup),
            "name" -> JsString(name),
            "dob" -> JsString(dob),
            "phoneNo" -> JsString(phoneNo),
            "email" -> JsString(email),
            "message" -> JsString("Profile Details Loaded")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could not load profile details")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could not load profile details")
          ).prettyPrint)
        }
      }
    }

  }
}
