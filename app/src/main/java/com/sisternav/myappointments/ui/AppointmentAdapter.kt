package com.sisternav.myappointments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sisternav.myappointments.R
import com.sisternav.myappointments.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(val appointments:ArrayList<Appointment>) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvAppointmentId = itemView.tvAppointmentId
        val tvDoctorName = itemView.tvDoctorName
        val tvScheduledDate = itemView.tvScheduledDate
        val tvScheduledTime = itemView.tvScheduledTime

    }
    // crear la lista a partir del XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment,
                parent,
                false
            )
        )
    }
    // Número de elementos
    override fun getItemCount() = appointments.size
    // Enlazar datos con la vista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.tvAppointmentId.text = holder.tvAppointmentId.context.getString(R.string.item_appointment_id, appointment.id)
        holder.tvDoctorName.text = appointment.doctorName
        holder.tvScheduledDate.text =holder.tvAppointmentId.context.getString(R.string.item_appointment_date, appointment.scheduledDate)
        holder.tvScheduledTime.text = holder.tvAppointmentId.context.getString(R.string.item_appointment_time, appointment.scheduledTime)
    }
}