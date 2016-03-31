package com.donoroncall.server.rest.controllers.authentication

/**
 * Created by anmol on 31/3/16.
 */


  import java.security.MessageDigest

  import com.donoroncall.server.BootStrapServer.mysqlClient
  import com.google.inject.Inject


  class PatientDetailsController @Inject()(sessionHandler: SessionHandler) {




    def getBloodGroup(userId: Long): String = {

      val query = "SELECT blood_group from recipients where recipientUserId ='" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val blood_group = resultSet.getString(1)

      blood_group

    }

    def getName(userId: Long): String = {

      val query = "SELECT patient_name from recipients where recipientUserId= '" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val name = resultSet.getString(1)

      name

    }
    def getHospitalName(userId: Long): String = {

      val query = "SELECT hospital_name from recipients where recipientUserId = '" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val hospital_name = resultSet.getString(1)

      hospital_name

    }

    def getPhone(userId: Long): String = {

      val query = "SELECT phoneNo from recipients where recipientUserId = '" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val phoneNo = resultSet.getString(1)

      phoneNo

    }



    def getPurpose(userId: Long): String = {

      val query = "SELECT purpose from recipients where recipientUserId = '" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val purpose = resultSet.getString(1)

      purpose

    }

    def getRequestCount(userId: Long): String = {

      val query = "SELECT request_count from recipients where recipientUserId = '" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val request_count = resultSet.getString(1)

      request_count

    }
    def getHowSoon(userId: Long): String = {

      val query = "SELECT how_soon from recipients where recipientUserId = '" + userId + "'"


      val resultSet = mysqlClient.getResultSet(query)
      val how_soon = resultSet.getString(1)

      how_soon

    }


    def updateProfile(userId: Long, name: String, bloodGroup: String, dob:String, phoneNo:String, email:String): Boolean = {

      val query = "UPDATE users SET name = '"+ name +"', dob = '"+dob+"', bloodGroup = '"+bloodGroup +"', phoneNo = '"+phoneNo +"', email = '"+email+"'  WHERE userId ='" + userId + "'"
      val resultSet = mysqlClient.getResultSet(query)
      if(resultSet.next() )  true
      else  false
    }





    def hash(text: String): String = {
      val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
      sha256.update(text.getBytes("UTF-8"))
      val digest = sha256.digest()
      String.format("%064x", new java.math.BigInteger(1, digest))
    }
  }