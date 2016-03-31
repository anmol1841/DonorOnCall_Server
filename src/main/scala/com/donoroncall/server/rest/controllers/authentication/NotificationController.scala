package com.donoroncall.server.rest.controllers.authentication

import java.security.MessageDigest

import com.donoroncall.server.BootStrapServer.mysqlClient
import com.google.inject.Inject


/**
 * Created by anmol on 23/3/16.
 */
class NotificationController  @Inject()(sessionHandler: SessionHandler) {


  def getBloodGroup(userId:Long): String = {

    val query = "SELECT blood_group from recipients where recipientUserId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val blood_group = resultSet.getString(1)

    blood_group

  }

  def getName(userId:Long): String = {

    val query = "SELECT patientName from recipients where recipientUserId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val name = resultSet.getString(1)

    name

  }

  def getHospitalName(userId:Long): String = {

    val query = "SELECT hospital_name from recipients where recipientUserId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val hospitalName = resultSet.getString(1)

    hospitalName

  }


  def getPhoneNo(userId:Long): String = {

    val query = "SELECT phoneNo from recipients where recipientUserId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val phoneNo = resultSet.getString(1)

    phoneNo

  }

  def getLatitude(userId:Long): String = {

    val query = "SELECT latitude from recipients where recipientUserId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val latitude = resultSet.getString(1)

    latitude

  }

  def getLongitude(userId:Long): String = {

    val query = "SELECT longitude from recipients where recipientUserId = '" + userId + "'"


    val resultSet = mysqlClient.getResultSet(query)
    val longitude = resultSet.getString(1)

    longitude

  }




  def hash(text: String): String = {
    val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
    sha256.update(text.getBytes("UTF-8"))
    val digest = sha256.digest()
    String.format("%064x", new java.math.BigInteger(1, digest))
  }
}