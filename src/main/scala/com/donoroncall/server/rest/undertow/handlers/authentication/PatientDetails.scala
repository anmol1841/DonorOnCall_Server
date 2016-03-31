package com.donoroncall.server.rest.undertow.handlers.authentication

/**
 * Created by anmol on 31/3/16.
 */

  import com.donoroncall.server.rest.controllers.authentication.{PatientDetailsController, SessionHandler}
  import com.google.inject.Inject
  import io.undertow.server.{HttpHandler, HttpServerExchange}
  import org.apache.commons.io.IOUtils
  import spray.json._


  class PatientsDetails @Inject()(patientDetailsController: PatientDetailsController, sessionHandler: SessionHandler ) extends HttpHandler {
    override def handleRequest(exchange: HttpServerExchange): Unit = {
      if (exchange.isInIoThread) {
        exchange.dispatch(this)
      } else {
        try {
          exchange.startBlocking()
          val request = new String(IOUtils.toByteArray(exchange.getInputStream))

          val requestJson = request.parseJson.asJsObject


          val userId = requestJson.getFields("userId").head.asInstanceOf[JsString].value.toLong




          val bloodGroup = patientDetailsController.getBloodGroup(userId)

          val hospital_name = patientDetailsController.getHospitalName(userId)

          val name = patientDetailsController.getName(userId)

          val purpose = patientDetailsController.getPurpose(userId)

          val phoneNo = patientDetailsController.getPhone(userId)

          val request_count = patientDetailsController.getRequestCount(userId)

          val how_soon = patientDetailsController.getHowSoon(userId)

          if (userId != null) {
            // to verify if this is the correct way
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("ok"),
              "userId" -> JsNumber(userId),
              "bloodGroup" -> JsString(bloodGroup),
              "hospital_name"->JsString(hospital_name),
              "name" -> JsString(name),
              "purpose" -> JsString(purpose),
              "phoneNo" -> JsString(phoneNo),
              "request_count" -> JsString(request_count),
              "how_soon"->JsString(how_soon),
              "message" -> JsString("Recipient Details Loaded")
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
