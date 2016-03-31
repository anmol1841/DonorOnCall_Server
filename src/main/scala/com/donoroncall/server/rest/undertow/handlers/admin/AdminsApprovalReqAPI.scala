package com.donoroncall.server.rest.undertow.handlers.admin

import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, AuthenticationController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
 * Created by Anmol on 9/3/16.
 */
class AdminsApprovalReqAPI @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val blood_group = requestJson.getFields("blood_group").head.asInstanceOf[JsString].value
        val admin_response = requestJson.getFields("admin_response").head.asInstanceOf[JsString].value
        val latitude = requestJson.getFields("latitude").head.asInstanceOf[JsString].value
        val longitude = requestJson.getFields("longitude").head.asInstanceOf[JsString].value
        val email = requestJson.getFields("email").head.asInstanceOf[JsString].value

        val userId = requestJson.getFields("userId").head.asInstanceOf[JsString].value.toLong

        if(admin_response== "yes"){
        val userI = authenticationController.addNewRecipientTable(blood_group, latitude, longitude, email, userId)
        if (userI) {


//TO
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("ok"),
              "message" -> JsString(" List of donors according to distance is ready ")
              // list is ready in the table userName_recipient

            ).prettyPrint)

           }else {

          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Request for blood Failed")
          ).prettyPrint)
        }}
        else if(admin_response=="no") {

          val userI = authenticationController.updateRecipientsTable(userId)

          if (userI) {



            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("ok"),
              "message" -> JsString(" recipients table updated as adminReply = no.")
              // list is ready in the table userName_recipient

            ).prettyPrint)

          }else {

            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("failed"),
              "message" -> JsString("Request for blood Failed")
            ).prettyPrint)
          }


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
