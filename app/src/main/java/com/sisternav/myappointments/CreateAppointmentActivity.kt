package com.sisternav.myappointments

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_create_appointment.*
import java.util.*

class CreateAppointmentActivity : AppCompatActivity() {
    private val selectedCalendar = Calendar.getInstance()
    private var selectedRadioButton: RadioButton? = null

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

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        val specialtyOptions = arrayOf("Specialty A", "Specialty B", "Specialty C")
        spinnerSpecialties.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, specialtyOptions)

        val doctorOptions = arrayOf("Doctor A", "Doctor B", "Doctor C")
        spinnerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorOptions)
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
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
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
        selectedRadioButton = null
        rgLeft.removeAllViews()
        rgRight.removeAllViews()

        val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM","4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener{view ->
                selectedRadioButton?.isChecked = false

                selectedRadioButton = view as RadioButton?
                selectedRadioButton?.isChecked = true
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
        if(cvStep2.visibility == View.VISIBLE){
            cvStep2.visibility = View.GONE
            cvStep1.visibility = View.VISIBLE
        }else if(cvStep1.visibility == View.VISIBLE){
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
