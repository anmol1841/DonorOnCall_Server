package com.donoroncall.server.rest.controllers.authentication

import java.security.MessageDigest
import java.util

import com.donoroncall.server.BootStrapServer.mysqlClient
import com.google.inject.Inject

/**
 * Created by vishnu on 20/1/16.
 */
class AuthenticationController @Inject()(sessionHandler: SessionHandler) {

  def login(email: String, password: String): String = {
    val query = "SELECT passwordHash,userId from users where email ='" + email + "'"
    val resultSet = mysqlClient.getResultSet(query)
    if (resultSet.next()) {
      val passwordHash = resultSet.getString(1)
      if (passwordHash == hash(password)) {
        sessionHandler.createSessionTokenForUser(resultSet.getLong(2))
      } else ""
    } else ""
  }


  def getEmail(userId: Long): String = {

    val query = "SELECT email from users where userId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val email = resultSet.getString(1)

    email

  }
  def addNewUser(email: String, password: String, name: String, blood_group: String, dob:String, latitude:String, longitude:String, phoneNo:String, usename:String): Boolean = {

    val query = "SELECT * from users where email ='" + email + "'"
    val resultSet = mysqlClient.getResultSet(query)
    if( !resultSet.next()){


        //TODO add more validations if user exists etc
        val insertQuery = "INSERT INTO users (email ,passwordHash, name, blood_group, dob, latitude, longitude, phoneNo, username) VALUES ('" + email + "','" + hash(password) + "','" + name  + "','"  + blood_group  + "','"  + dob  + "','"  + latitude  + "','"  + longitude + "' , '" + phoneNo+ "' , '"+usename  + "','"  +  "')"
        mysqlClient.executeQuery(insertQuery)

    }
  else  false}



  def addNewRecipient(blood_group: String, email:String, hospital_name:String, patient_name: String, purpose: String, request_count:String, how_Soon:String, phoneNo:String, latitude:String, longitude:String, recipientUserId:Long): String={
    val insertQuery= " INSERT INTO recipients (blood_group, email, hospitalName, patientName,purpose, request_count, howSoon, phoneNo, latitude, longitude, recipientUserId) VALUES ('"+  blood_group+"','"+ email+"','"+ hospital_name+"','"+ patient_name +"','"+ purpose +"','"+ request_count +"','"+ how_Soon+" , "+ phoneNo+" , "+ latitude+" , "+ longitude+"','"+ recipientUserId +")"
    mysqlClient.executeQuery(insertQuery)
   ""
  }


  def addNewRecipientTable(blood_group:String, latitude:String, longitude:String, email:String, userId:Long ): Boolean ={
    val selectQuery = "SELECT username, latitude, longitude, userId from users where blood_group='" + blood_group + "'"
    val resultSet = mysqlClient.getResultSet(selectQuery)
    val updateQuery =" UPDATE recipients SET adminReply = 'yes' WHERE userId = "+ userId+";"
    mysqlClient.getResultSet(updateQuery)
    val createQuery = "CREATE TABLE "+ userId+ "_Recipient { username VARCHAR(20), distance INTEGER, donorUserId BIGINT } "
    mysqlClient.getResultSet(createQuery)
    if(resultSet.next()){
    while ( resultSet.next()){
      // keeping lat1, long1 to be of the recipient and lat2 long2 to be of each donor from the above resultset
      val donorName = resultSet.getString(1)
      val donorUserId = resultSet.getLong(4)
      val lat2 = resultSet.getDouble(2)
      val long2 = resultSet.getDouble(3)
      val latitudeDouble = latitude.toDouble
      val longitudeDouble = longitude.toDouble

      val dist = calculateDistance(latitudeDouble, longitudeDouble, lat2, long2)

      val insertQuery = " INSERT INTO "+ userId+"_Recipient (username, distance, donorUserId) VALUES ('"+ donorName+"', " + dist+"', " + donorUserId+")"
      mysqlClient.getResultSet(insertQuery)

      val sortQuery = " SELECT * FROM "+ userId+"_Recipient ORDER BY dist;"
      mysqlClient.getResultSet(sortQuery)



    }
       true
    } else false
  }

  def calculateDistance(lat1: Double,long1: Double, lat2: Double, long2:Double): Double ={

    val theta = long1 - long2
    val radTheta = Math.toRadians(theta)
    val radlat1 = Math.toRadians(lat1)
    val radlat2 = Math.toRadians(lat2)
    val radlong1 = Math.toRadians(long1)
    val radlong2 = Math.toRadians(long2)
    var dist = Math.sin(radlat1)*Math.sin(radlat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(radTheta)
    dist = Math.acos(dist)
    dist = Math.toDegrees(dist)
    dist = dist * 60 * 1.85315;


     dist

  }

  def updateRecipientsTable(userId:Long):Boolean={
    val updateQuery =" UPDATE recipients SET adminReply = 'no' WHERE userId = "+ userId+";"
     mysqlClient.getResultSet(updateQuery)
   true
  }

  def getDonors(userId:Long):util.ArrayList[String]= {

    val selectQuery = "SELECT userId from " + userId + "_Recipient"
    val resultSet = mysqlClient.getResultSet(selectQuery)


    val z = new util.ArrayList[String]


    z.add(resultSet.getString(1): String)
    z
  }

  def getRecipients():util.ArrayList[String]= {

    val selectQuery = "SELECT recipientUserId from recipients WHERE adminReply = 'null'; "
    val resultSet = mysqlClient.getResultSet(selectQuery)

    val z = new util.ArrayList[String]


    z.add(resultSet.getString(1): String)
    z
  }





  def processComplete(userId: Long, donationStatus:String, donorName:String, donorEmail:String, Units: Int, date:String, blood_group:String): Boolean ={
    val query = "DROP TABLE " + userId+"_Recipient"
    mysqlClient.getResultSet(query)
    val insertQuery = "INSERT INTO process_complete (userId, donationStatus, donorName, donorEmail, units , blood_group, date ) VALUES ('" + userId + "','" + donationStatus + "','" + donorName + "','"  + Units + "','" + blood_group  + "','"  + date  + "')"
    mysqlClient.getResultSet(insertQuery)

     true
  }

  def hash(text: String): String = {
    val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
    sha256.update(text.getBytes("UTF-8"))
    val digest = sha256.digest()
    String.format("%064x", new java.math.BigInteger(1, digest))
  }
}
