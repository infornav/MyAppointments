package com.sisternav.myappointments.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.sisternav.myappointments.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.sisternav.myappointments.util.PreferenceHelper.get
import com.sisternav.myappointments.util.PreferenceHelper.set
import com.sisternav.myappointments.R
import com.sisternav.myappointments.io.ApiService
import com.sisternav.myappointments.io.response.LoginResponse
import com.sisternav.myappointments.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy{
        ApiService.create()
    }
    private val snackBar by lazy{
        Snackbar.make(mainLayout,
            R.string.press_back_again,Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val session = preferences.getBoolean("session",false)
*/
        val preferences =
            PreferenceHelper.defaultPrefs(this)

        if(preferences["jwt", ""].contains("."))
            goToMenuActivity()

        btnLogin.setOnClickListener {
            performLogin()
        }

        tvGoToRegister.setOnClickListener{
            Toast.makeText(this, getString(R.string.please_fill_your_register_data), Toast.LENGTH_SHORT).show()

            val intent = Intent(this,
                RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if(email.trim().isEmpty() || password.trim().isEmpty()){
            toast(getString(R.string.error_empty_credentials))
            return
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) { // [200...300)
                    val loginResponse  = response.body()
                    if(loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }

                    if(loginResponse.success){
                        creaateSessionPreference(loginResponse.jwt)
                        toast(getString(R.string.welcome_name,loginResponse.user.name))
                        goToMenuActivity()
                    }else{
                        toast(getString(R.string.error_invalid_credentials))
                    }
                }else{
                    toast(getString(R.string.error_login_response))
                }
            }
        })
    }

    private fun creaateSessionPreference(jwt:String) {
        /*val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true)
        editor.apply()*/
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["jwt"] = jwt
    }

    private fun goToMenuActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if(snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}
