package com.sisternav.myappointments.model
/*
RESPUESTA DEL SERVIDOR
"id": 1,
        "name": "Abel Roque",
        "email": "ruffo_roque@hotmail.com",
        "dni": null,
        "address": null,
        "phone": null,
        "role": "admin"
 */
data class User (
    val id:Int,
    val name:String,
    val email:String,
    val dni:String,
    val address:String,
    val phone:String,
    val role:String
){
    override fun toString(): String {
        return name
    }
}