package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, AuthenticationController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
 * Created by Anmol on 9/3/16.
 */
class RequestForBlood @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val blood_group = requestJson.getFields("bloodGroup").head.asInstanceOf[JsString].value
        val authToken = requestJson.getFields("token").head.asInstanceOf[JsString].value

        val userId  = sessionHandler.getUserIdForSession(authToken)

        val email = authenticationController.getEmail(userId)



        val hospital_name = requestJson.getFields("hospitalName").head.asInstanceOf[JsString].value
        val patient_Name = requestJson.getFields("patientName").head.asInstanceOf[JsString].value
        val purpose = requestJson.getFields("purpose").head.asInstanceOf[JsString].value
        val units = requestJson.getFields("units").head.asInstanceOf[JsString].value
        val how_Soon = requestJson.getFields("howSoon").head.asInstanceOf[JsString].value
        val phoneNo = requestJson.getFields("phoneNo").head.asInstanceOf[JsString].value
        val latitude = requestJson.getFields("latitude").head.asInstanceOf[JsString].value
        val longitude = requestJson.getFields("longitude").head.asInstanceOf[JsString].value

        val userI = authenticationController.addNewRecipient(blood_group, email, hospital_name, patient_Name, purpose, units, how_Soon,phoneNo, latitude, longitude, userId)

        if (userI == "") {

          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "message" -> JsString("New Recipient Added, waiting for Admin approval.")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Request for blood Failed")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Request for blood Failed")
          ).prettyPrint)
        }
      }
    }

  }
}
