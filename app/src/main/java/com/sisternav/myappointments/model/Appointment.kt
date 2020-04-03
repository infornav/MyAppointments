package com.sisternav.myappointments.model

data class Appointment (
    val id:Int,
    val doctorName:String,
    val scheduledDate:String,
    val scheduledTime:String
)
