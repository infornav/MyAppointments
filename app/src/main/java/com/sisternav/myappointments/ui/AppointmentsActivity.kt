package com.sisternav.myappointments.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.sisternav.myappointments.R
import com.sisternav.myappointments.io.ApiService
import com.sisternav.myappointments.model.Appointment
import com.sisternav.myappointments.util.PreferenceHelper
import com.sisternav.myappointments.util.PreferenceHelper.get
import com.sisternav.myappointments.util.toast
import kotlinx.android.synthetic.main.activity_appointments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentsActivity : AppCompatActivity() {
    private val apiService: ApiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        loadAppointments()

        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = appointmentAdapter
    }

    private fun loadAppointments(){
        val jwt = preferences["jwt",""]
        val call = apiService.getAppointments("Bearer $jwt")

        call.enqueue(object: Callback<ArrayList<Appointment>>{
            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                Log.d("TOKEN_ERROR","error: ${t.message}")
                toast(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ArrayList<Appointment>>,
                response: Response<ArrayList<Appointment>>
            ) {
                Log.d("TOKEN_ERROR","codigo: ${response.code().toString()}")
                Log.d("TOKEN_ERROR","mensaje: ${response.message()}")
                if(response.isSuccessful){
                    response.body()?.let{
                        appointmentAdapter.appointments = it
                        appointmentAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }
}
