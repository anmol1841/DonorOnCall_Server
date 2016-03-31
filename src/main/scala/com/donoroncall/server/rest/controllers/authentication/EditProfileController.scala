package com.donoroncall.server.rest.controllers.authentication

import java.security.MessageDigest

import com.donoroncall.server.BootStrapServer.mysqlClient
import com.google.inject.Inject

/**
 * Created by anmol on 11/3/16.
 */
class EditProfileController @Inject()(sessionHandler: SessionHandler) {




  def getBloodGroup(userId: Long): String = {

    val query = "SELECT blood_group from users where userId ='" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val blood_group = resultSet.getString(1)

     blood_group

  }

  def getName(userId: Long): String = {

    val query = "SELECT name from users where userId= '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val name = resultSet.getString(1)

     name

  }
  def getDob(userId: Long): String = {

    val query = "SELECT dob from users where userId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val dob = resultSet.getString(1)

     dob

  }

  def getPhone(userId: Long): String = {

    val query = "SELECT phoneNo from users where userId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val email = resultSet.getString(1)

    email

  }



  def getEmail(userId: Long): String = {

    val query = "SELECT email from users where userId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val email = resultSet.getString(1)

     email

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