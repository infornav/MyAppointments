package com.sisternav.myappointments.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.sisternav.myappointments.R
import com.sisternav.myappointments.io.ApiService
import com.sisternav.myappointments.util.PreferenceHelper
import com.sisternav.myappointments.util.PreferenceHelper.get
import com.sisternav.myappointments.util.PreferenceHelper.set
import com.sisternav.myappointments.util.toast
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private val apiService: ApiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val storeToken = intent.getBooleanExtra("store_token",false)
        if(storeToken){
            storeToken()
        }

        btnCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
        }

        btnMyAppointments.setOnClickListener {
            val intent = Intent(this, AppointmentsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener{
            performLogout()
        }
    }

    private fun storeToken(){
        val jwt = preferences["jwt",""]
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val deviceToken = instanceIdResult.token
            //Log.d("FCMService", deviceToken)
            val call = apiService.postToken("Bearer $jwt", deviceToken)

            call.enqueue(object: Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Log.d(TAG,"Token registrado correctamente")
                    } else {
                        Log.d(TAG,"Hubo un problema al registrar el token")
                    }
                }
            })
        }
    }

    private fun performLogout(){
        val jwt = preferences["jwt",""]
        val call = apiService.postLogout("Bearer $jwt")

        call.enqueue(object: Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()

                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun clearSessionPreference() {
        /*val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", false)
        editor.apply()*/
        preferences["jwt"] = ""
    }

    companion object {

        private const val TAG = "MenuActivity"
    }
}
