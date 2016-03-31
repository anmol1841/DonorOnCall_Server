package com.donoroncall.server.rest.undertow.handlers.authentication

/**
 * Created by anmol on 31/3/16.
 */


  import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, AuthenticationController}
  import com.google.inject.Inject
  import io.undertow.server.{HttpHandler, HttpServerExchange}
  import org.apache.commons.io.IOUtils
  import spray.json._

  class PendingAdminApproval @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
    override def handleRequest(exchange: HttpServerExchange): Unit = {
      if (exchange.isInIoThread) {
        exchange.dispatch(this)
      } else {
        try {
          exchange.startBlocking()
          val request = new String(IOUtils.toByteArray(exchange.getInputStream))

          val requestJson = request.parseJson.asJsObject


          val z = authenticationController.getRecipients().toArray.asInstanceOf[Array[String]].map(JsString(_))toVector



          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("Complete"),
            "message" -> JsString("This array contains the list of userIds of recipients pending for admins reply "),
            "array" -> JsArray(z)
          ).prettyPrint)
        }

        catch {
          case e: Exception => {
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("failed"),
              "message" -> JsString("could not load recipients details")
            ).prettyPrint)
          }
        }
      }

    }
  }