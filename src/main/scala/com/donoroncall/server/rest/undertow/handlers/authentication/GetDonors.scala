package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, AuthenticationController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._
/**
 * Created by Anmol on 28/3/16.
 */
class GetDonors @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
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

        //val z = authenticationController.getDonors(username).toArray
        val z = authenticationController.getDonors(userId).toArray.asInstanceOf[Array[String]].map(JsString(_))toVector



        exchange.getResponseSender.send(JsObject(
          "status" -> JsString("Complete"),
          "message" -> JsString("This array contains the list of donor username sorted with distance to the recipient"),
          "array" -> JsArray(z)
        ).prettyPrint)
      }

      catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("could not load donor details")
          ).prettyPrint)
        }
      }
    }

  }
}