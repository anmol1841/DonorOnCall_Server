package com.donoroncall.server.rest.undertow.handlers.authentication

/**
 * Created by anmol on 23/3/16.
 */

import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, NotificationController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._


class DonorSideNotificationHandler @Inject()(notificationController:NotificationController, sessionHandler: SessionHandler ) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))


        val requestJson = request.parseJson.asJsObject



        val userId = requestJson.getFields("token").head.asInstanceOf[JsString].value.toLong
        // Recipient userId


        val bloodGroup = notificationController.getBloodGroup(userId)
        val patientName = notificationController.getName(userId)
        val phoneNo = notificationController.getPhoneNo(userId)
        val hospitalName = notificationController.getHospitalName(userId)
        val latitude = notificationController.getLatitude(userId)
        val longitude = notificationController.getLongitude(userId)




        if (userId != null) {
          // to verify if this is the correct way
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "patientName" -> JsString(patientName),
            "bloodGroup" -> JsString(bloodGroup),
            "phoneNo" -> JsString(phoneNo),
            "hospitalName" -> JsString(hospitalName),
            "latitude" -> JsString(latitude),
            "langitude" -> JsString(longitude),


            "message" -> JsString("Blood Required for this patient")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could not load recipient details")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could not load recipient details")
          ).prettyPrint)
        }
      }
    }

  }
}

