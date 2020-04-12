package com.sisternav.myappointments.model

import com.google.gson.annotations.SerializedName
import java.io.FileDescriptor

/*
{
    "id": 84,
    "description": "Reprehenderit tenetur tempora nesciunt eveniet sunt nisi.",
    "scheduled_date": "2020-01-24",
    "type": "Operación",
    "status": "Cancelada",
    "created_at": "2020-04-05T04:08:56.000000Z",
    "scheduled_time_12": "1:33 PM",
    "specialty": {
        "id": 2,
        "name": "Pediatría"
    },
    "doctor": {
        "id": 56,
        "name": "Josiah Kessler"
    }
}
 */
data class Appointment (
    val id:Int,
    val description: String,
    val type: String,
    val status: String,

    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time_12") val scheduledTime: String,
    @SerializedName("created_at") val createdAt: String,

    val specialty: Specialty,
    val doctor: Doctor
)
