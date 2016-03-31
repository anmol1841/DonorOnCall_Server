package com.donoroncall.server.rest.undertow

import com.donoroncall.server.rest.undertow.handlers.admin.AdminsApprovalReqAPI
import com.donoroncall.server.rest.undertow.handlers.authentication._
import com.donoroncall.server.rest.undertow.routes.ApiRoutes
import com.google.inject.Inject
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.PathHandler

/**
  * Created by vishnu on 20/1/16.
  */
class Routes @Inject()(
                        apiRoutes: ApiRoutes,
                        loginApiHandler: LoginApiHandler,
                        logoutApiHandler: LogoutApiHandler,
                        registrationApiHandler: RegistrationApiHandler,
                        editProfileHandler: EditProfileHandler,
                        donorSideNotificationHandler: DonorSideNotificationHandler,
                      updateProfileHandler: UpdateProfileHandler,
                      processCompletion: ProcessCompletion,
                      requestForBlood: RequestForBlood,
                      getDonors: GetDonors,
                        pendingAdminsApproval:PendingAdminApproval,
                        patientDetails:PatientsDetails,
                      adminsApprovalReqAPI: AdminsApprovalReqAPI



                      ) {

  def getAllHandlers: HttpHandler = {
    new PathHandler()
      .addExactPath("/login", loginApiHandler)
      .addExactPath("/editProfile", editProfileHandler)
      .addExactPath("/requestForBlood", requestForBlood)
      .addExactPath("/processComplete",processCompletion )
      .addExactPath("/updateProfile", updateProfileHandler)
      .addExactPath("/donorNotification", donorSideNotificationHandler)
      .addExactPath("/logout", logoutApiHandler)
      .addExactPath("/adminApproval", adminsApprovalReqAPI)
      .addExactPath("/register", registrationApiHandler)
      .addExactPath("/getDonors", getDonors)
      .addExactPath("/pendingAdminsApproval", pendingAdminsApproval)
      .addExactPath("/patientDetails", patientDetails)
      .addPrefixPath("/api/", apiRoutes.pathHandler)
  }
}
