package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.AuthenticationController
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
  * Created by vishnu on 20/1/16.
  */
class RegistrationApiHandler @Inject()(authenticationController: AuthenticationController) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val username = requestJson.getFields("username").head.asInstanceOf[JsString].value
        val name = requestJson.getFields("name").head.asInstanceOf[JsString].value
        val dob = requestJson.getFields("email").head.asInstanceOf[JsString].value
        val bloodGroup = requestJson.getFields("blood_group").head.asInstanceOf[JsString].value
        val password = requestJson.getFields("password").head.asInstanceOf[JsString].value

        val latitude = requestJson.getFields("latitude").head.asInstanceOf[JsString].value
        val longitude = requestJson.getFields("longitude").head.asInstanceOf[JsString].value
        val phoneNo = requestJson.getFields("phoneNo").head.asInstanceOf[JsString].value
        val email = requestJson.getFields("email").head.asInstanceOf[JsString].value


        val userId = authenticationController.addNewUser(email, password, name, bloodGroup, dob, latitude, longitude, phoneNo, username)

        if (userId) {
          //TODO add logic for Successful Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "message" -> JsString("Registration successful")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Registration Failed")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Registration Failed")
          ).prettyPrint)
        }
      }
    }

  }
}
