package com.sisternav.myappointments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.sisternav.myappointments.R
import com.sisternav.myappointments.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    var appointments = ArrayList<Appointment>()

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(appointment: Appointment) = with (itemView){
            tvAppointmentId.text = context.getString(R.string.item_appointment_id, appointment.id)
            tvDoctorName.text = appointment.doctor.name
            tvScheduledDate.text = context.getString(R.string.item_appointment_date, appointment.scheduledDate)
            tvScheduledTime.text = context.getString(R.string.item_appointment_time, appointment.scheduledTime)

            tvSpecialty.text = appointment.specialty.name
            tvDescription.text = appointment.description
            tvStatus.text = appointment.status
            tvType.text = appointment.type
            tvCreatedAt.text = context.getString(R.string.item_appointment_created_at, appointment.createdAt)

            ibExpand.setOnClickListener {
                TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                if (linearLayoutDetails.visibility == View.VISIBLE) {
                    linearLayoutDetails.visibility = View.GONE
                    ibExpand.setImageResource(R.drawable.ic_expand_more)
                } else {
                    linearLayoutDetails.visibility = View.VISIBLE
                    ibExpand.setImageResource(R.drawable.ic_expand_less)
                }
            }
        }
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

        holder.bind(appointment)
    }
}