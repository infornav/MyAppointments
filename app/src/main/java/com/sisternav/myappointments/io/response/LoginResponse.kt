package com.sisternav.myappointments.io.response

import com.sisternav.myappointments.model.User

data class LoginResponse (
    val success:Boolean,
    val user: User,
    val jwt: String
)