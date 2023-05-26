package com.example.geotrackfamily.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.FindFriendItemBinding
import com.example.geotrackfamily.databinding.NotificationItemBinding
import com.example.geotrackfamily.models.Notification
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.utility.Utils
import java.text.SimpleDateFormat
import java.util.*




class NotificationAdapter (val context: Context, var list: List<Notification>, val observer: UIObserverGeneric<Notification>):
    RecyclerView.Adapter<NotificationAdapter.holderAdapter>() {

    class holderAdapter(binding: NotificationItemBinding): RecyclerView.ViewHolder(binding.root){
        private val binding = binding
        fun binData(notification: Notification, observer: UIObserverGeneric<Notification>){
            binding.tvtitle.setText(notification.title)
            binding.tvmessage.setText(notification.message)
            try {
                binding.tvDate.setText(convertDateFormat(inputDate = notification.created_at))
            }catch (e: java.lang.Exception){

            }
        }
        fun convertDateFormat(inputDate: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderAdapter {
        val binding = NotificationItemBinding.bind(LayoutInflater.from(context).inflate(R.layout.notification_item,parent, false))
        return holderAdapter(
            binding = binding
        )
    }

    override fun onBindViewHolder(holder: holderAdapter, position: Int) {
        holder.binData(notification = list[position], observer = observer)
    }

    override fun getItemCount() = list.size

    fun setNewData(arrayList: List<Notification>) {
        list = arrayList
        notifyDataSetChanged()
    }

    fun deleteElement(position: Int){
        notifyItemRemoved(position)
    }

}