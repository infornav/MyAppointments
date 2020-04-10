package com.sisternav.myappointments.io

import com.sisternav.myappointments.model.Doctor
import com.sisternav.myappointments.model.Specialty
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("specialties")
    fun getSpecialties() : Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")
    fun getDoctorbySpecialty(@Path("specialty") specialtyId: Int) : Call<ArrayList<Doctor>>

    @GET("schedule/hours")
    fun getHours(@Path("doctor_id") doctorId: Int, @Path("date") date: String) : Call<ArrayList<Doctor>>

    companion object Factory{
        private const val BASE_URL = "http://165.227.186.92/api/"

        fun create(): ApiService{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}