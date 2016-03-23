package com.donoroncall.server.rest.controllers.authentication

import java.security.MessageDigest

import com.donoroncall.server.BootStrapServer.mysqlClient
import com.google.inject.Inject

/**
 * Created by vishnu on 20/1/16.
 */
class AuthenticationController @Inject()(sessionHandler: SessionHandler) {

  def login(username: String, password: String): String = {
    val query = "SELECT passwordHash,userId from users where username='" + username + "'"
    val resultSet = mysqlClient.getResultSet(query)
    if (resultSet.next()) {
      val passwordHash = resultSet.getString(1)
      if (passwordHash == hash(password)) {
        sessionHandler.createSessionTokenForUser(resultSet.getLong(2))
      } else ""
    } else ""
  }

  def addNewUser(username: String, password: String, name: String, blood_group: String, dob:String, confirmPassword:String, latitude:String, longitude:String): Boolean = {

    val query = "SELECT * from users where username='" + username + "'"
    val resultSet = mysqlClient.getResultSet(query)
    if( resultSet.next() == null){

      if(password == confirmPassword){
        //TODO add more validations if user exists etc // done now above query to confirm password and check if user exists
        // TO ADD LOGIG to add phone no and email at this point while adding user
        // TO DO  ADD logic to get lat long at this point, and update in the table
        val insertQuery = "INSERT INTO users (userName,passwordHash, name, blood_group, dob, latitude, longitude) VALUES ('" + username + "','" + hash(password) + "','" + name  + "','"  + blood_group  + "','"  + dob  + "','"  + latitude  + "','"  + longitude  + "','"  +  "')"
        mysqlClient.executeQuery(insertQuery)
      }else  false
    }
  else  false}



  def addNewRecipient(blood_group: String, username:String, hospital_name:String, patient_name: String, purpose: String, request_count:String, how_Soon:String, phoneNo:String, latitude:String, longitude:String): String={
    val insertQuery= " INSERT INTO recipients (blood_group, username, hospitalName, patientName,purpose, request_count, howSoon, phoneNo, latitude, longitude) VALUES ('"+  blood_group+"','"+ username+"','"+ hospital_name+"','"+ patient_name +"','"+ purpose +"','"+ request_count +"','"+ how_Soon+" , "+ phoneNo+" , "+ latitude+" , "+ longitude +")"
    mysqlClient.executeQuery(insertQuery)
   ""
  }


  def addNewRecipientTable(blood_group:String,adminResponse:String, latitude:String, longitude:String, username:String): Boolean ={
    val selectQuery = "SELECT username, latitude, longitude from users where blood_group='" + blood_group + "'"
    val resultSet = mysqlClient.getResultSet(selectQuery)
    val createQuery = "CREATE TABLE "+ username+ "_Recipient { username VARCHAR(20), distance INTEGER } "
    mysqlClient.getResultSet(createQuery)
    if(resultSet.next()!=null){
    while ( resultSet.next() != null){
      // keeping lat1, long1 to be of the recipient and lat2 long2 to be of each donor from the above resultset
      val donorName = resultSet.getString(1)
      val lat2 = resultSet.getDouble(2)
      val long2 = resultSet.getDouble(3)
      val latitudeDouble = latitude.toDouble
      val longitudeDouble = longitude.toDouble

      val dist = calculateDistance(latitudeDouble, longitudeDouble, lat2, long2)

      val insertQuery = " INSERT INTO "+ username+"_Recipient (username, distance) VALUES ('"+ donorName+"', " + dist+")"
      mysqlClient.getResultSet(insertQuery)

      val sortQuery = " SELECT * FROM "+ username+"_Recipient ORDER BY dist;"
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
  def processComplete(userName: String, donationStatus:String, donorUserName:String, noOfUnits: Int, date:String): Boolean ={
    val query = "DROP TABLE " + userName+"_Recipient"
    mysqlClient.getResultSet(query)
    val insertQuery = "INSERT INTO users (username, donationStatus, donorUserName, blood_group, date ) VALUES ('" + userName + "','" + donationStatus + "','" + donorUserName + "','"  + noOfUnits  + "','"  + date  + "')"
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
