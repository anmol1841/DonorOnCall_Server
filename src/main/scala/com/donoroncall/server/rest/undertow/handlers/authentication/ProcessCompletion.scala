package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, AuthenticationController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
 * Created by Anmol on 10/3/16.
 */
class ProcessCompletion @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
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

        val donationStatus = requestJson.getFields("donationStatus").head.asInstanceOf[JsString].value
        val donorName = requestJson.getFields("donorName").head.asInstanceOf[JsString].value
        val donorEmail = requestJson.getFields("donorEmail").head.asInstanceOf[JsString].value
        val noOfUnits = requestJson.getFields("noOfUnits").head.asInstanceOf[JsString].value.toInt
        val date = requestJson.getFields("date").head.asInstanceOf[JsString].value
        val blood_group = requestJson.getFields("date").head.asInstanceOf[JsString].value

        val userI = authenticationController.processComplete(userId, donationStatus, donorName, donorEmail, noOfUnits, date, blood_group)

        if (userI) {

          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("Complete"),
            "message" -> JsString("Donation Process Complete.")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Process Completion Failed")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Process Completion Failed")
          ).prettyPrint)
        }
      }
    }

  }
}
