package com.sisternav.myappointments.ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.sisternav.myappointments.R
import com.sisternav.myappointments.io.ApiService
import com.sisternav.myappointments.model.Doctor
import com.sisternav.myappointments.model.Schedule
import com.sisternav.myappointments.model.Specialty
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {
    private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioButton: RadioButton? = null

    private val apiService:ApiService by lazy{
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            if(etDescription.text.toString().length < 3){
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener{
            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    //etScheduledDate.error = getString(R.string.validate_appointment_date)
                    Snackbar.make(createAppointmentLinearLayout,
                        R.string.validate_appointment_date,Snackbar.LENGTH_SHORT).show()
                }
                selectedTimeRadioButton == null -> {
                    Snackbar.make(createAppointmentLinearLayout,
                        R.string.validate_appointment_time,Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    showAppointmentDataToConfirm()
                    cvStep2.visibility = android.view.View.GONE
                    cvStep3.visibility = android.view.View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        loadSpecialties()
        listenSpecialtyChanges()
        listenDoctorAndDateChanges()
    }

    private fun loadHours(doctorId:Int, date:String){
        val call = apiService.getHours(doctorId, date)
        call.enqueue(object: Callback<Schedule>{
            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_hours), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful) { // [200...300)
                    val schedule = response.body()
                    Toast.makeText(this@CreateAppointmentActivity, "morning: ${schedule?.morning?.size}, afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        //Toast.makeText(this,"doctor: $doctorId, date: $date",Toast.LENGTH_SHORT).show()
    }

    private fun listenDoctorAndDateChanges(){
        //doctors
        spinnerDoctors.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val doctor = adapter?.getItemAtPosition(position) as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }
        }

        //scheduled date
        etScheduledDate.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }
        })
    }

    private fun listenSpecialtyChanges(){
        spinnerSpecialties.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                loadDoctors(specialty.id)
            }

        }
    }

    private fun loadDoctors(specialtyId:Int){
        val call = apiService.getDoctorbySpecialty(specialtyId)
        call.enqueue(object: Callback<java.util.ArrayList<Doctor>>{
            override fun onFailure(call: Call<java.util.ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_doctors), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<java.util.ArrayList<Doctor>>,
                response: Response<java.util.ArrayList<Doctor>>
            ) {
                if (response.isSuccessful) { // [200...300)
                    response.body()?.let {
                        val doctors = it.toMutableList()
                        spinnerDoctors.adapter = ArrayAdapter<Doctor>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, doctors)
                    }
                    /*
                    val doctors = response.body()
                    spinnerDoctors.adapter = ArrayAdapter<Doctor>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, doctors)*/
                }
            }
        })
    }

    private fun loadSpecialties(){
        val call = apiService.getSpecialties()
        call.enqueue(object: Callback<ArrayList<Specialty>> {
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_specialties), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>, response: Response<ArrayList<Specialty>>) {
                if (response.isSuccessful) { // [200...300)
                    response.body()?.let {
                        val specialties = it.toMutableList()
                        spinnerSpecialties.adapter = ArrayAdapter<Specialty>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, specialties)
                    }
                   /*val specialties = response.body()
                    spinnerSpecialties.adapter = ArrayAdapter<Specialty>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, specialties!!)*/
                }
            }
        })
    }

    private fun showAppointmentDataToConfirm() {
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioType = rgType.checkedRadioButtonId
        val selectedType = rgType.findViewById<RadioButton>(selectedRadioType)
        tvConfirmType.text = selectedType.text.toString()

        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmScheduledDate.text = etScheduledDate.text.toString()
        tvConfirmScheduledTime.text = selectedTimeRadioButton?.text.toString()
    }

    fun onClickScheduledDate(v:View?){
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        Log.d("mes-",month.toString())

        val listener = DatePickerDialog.OnDateSetListener{ datePicker, y, m, d ->
            //Toast.makeText(this,"$y-$m-$d",Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y,m,d)

            etScheduledDate.setText(
                resources.getString(
                    R.string.date_format,
                    y.toString(),
                    (m + 1).twoDigits(),
                    d.twoDigits()
                )
            )
            //etScheduledDate.error = null
            displayRadioButtons()
        }
        //new dialog
        val dpDialog = DatePickerDialog(this, listener,year,month, dayOfMonth)
        val datePicker = dpDialog.datePicker

        //set limits
        val calendar = Calendar.getInstance() //fecha actual
        calendar.add(Calendar.DAY_OF_MONTH,1)
        datePicker.minDate =  calendar.timeInMillis// +1 now
        calendar.add(Calendar.DAY_OF_MONTH,29)
        datePicker.maxDate = calendar.timeInMillis//+30 now

        //show dialog
        dpDialog.show()
    }

    private fun displayRadioButtons() {
//        rgTimes.clearCheck()
//        rgTimes.removeAllViews()
        selectedTimeRadioButton = null
        rgLeft.removeAllViews()
        rgRight.removeAllViews()

        val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM","4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener{view ->
                selectedTimeRadioButton?.isChecked = false

                selectedTimeRadioButton = view as RadioButton?
                selectedTimeRadioButton?.isChecked = true
            }

            if(goToLeft)
                rgLeft.addView(radioButton)
            else
                rgRight.addView(radioButton)

            goToLeft = !goToLeft
        }
    }

    private fun Int.twoDigits(): String {
        val s = if (this <= 9) "0$this" else this.toString()
        return s
    }

    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE
            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_create_appointment_exit_title))
                builder.setMessage(getString(R.string.dialog_create_appoitnment_exit_message))
                builder.setPositiveButton(getString(R.string.dialog_create_appoitnment_exit_positive_btn)) { _, _ ->
                    finish()
                }
                builder.setNegativeButton(getString(R.string.dialog_create_appoitnment_exit_negative_btn)) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}
