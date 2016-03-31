package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.{SessionHandler, AuthenticationController}
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import org.apache.commons.io.IOUtils
import org.slf4j.{Logger, LoggerFactory}
import spray.json._

/**
  * Created by vishnu on 20/1/16.
  */
class LoginApiHandler @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
  val LOG: Logger = LoggerFactory.getLogger(this.getClass)

  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {

        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val email = requestJson.getFields("email").head.asInstanceOf[JsString].value
        val password = requestJson.getFields("password").head.asInstanceOf[JsString].value

        val authToken = authenticationController.login(email, password)

        if (authToken != "") {
          exchange.getResponseHeaders.add(new HttpString("auth-token"), authToken)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "token" -> JsString(authToken),
            "message" -> JsString("Login successful")
          ).prettyPrint)

        } else {
          //TODO add logic for Login failure
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Invalid userName and password")
          ).prettyPrint)
        }
      } catch {
        case e: Exception =>
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Server Exception")
          ).prettyPrint)
      }
    }

  }
}
