package com.example.geotrackfamily.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.geotrackfamily.databinding.FriendAlertBinding
import com.example.geotrackfamily.R
import com.example.geotrackfamily.adapter.FriendGeofenceAdapter
import com.example.geotrackfamily.adapter.FriendsRequestAdapter
import com.example.geotrackfamily.databinding.AddGeozoneAlertBinding
import com.example.geotrackfamily.databinding.GeofenceFriendAlertBinding
import com.example.geotrackfamily.databinding.HourLocationAlertBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.GeofenceFriend
import com.example.geotrackfamily.observer.*
import com.example.geotrackfamily.utility.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GeoDialogs {
    companion object {
        fun friends_request_dialog(context: Context, observer: UIObserverFriendRequest, friendsR: ArrayList<Friend>){
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context).setCancelable(false).create()
            val binding = FriendAlertBinding.bind(LayoutInflater.from(context).inflate(R.layout.friend_alert,null))

            var adapter = FriendsRequestAdapter(context = context, list =  friendsR, observer = observer, alertDialogBuilder = alertDialogBuilder)
            binding.rvFriends.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            binding.rvFriends.adapter = adapter
            alertDialogBuilder.setButton(Dialog.BUTTON_NEGATIVE,context.resources.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })

            alertDialogBuilder.setView(binding.root)
            alertDialogBuilder.show()
            alertDialogBuilder.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.red))
        }
        fun friends_geozone_dialog(friend: Friend, context: Context, observer: UIObserverFriendGeoZone){
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context).setCancelable(false).create()
            val binding = AddGeozoneAlertBinding.bind(LayoutInflater.from(context).inflate(R.layout.add_geozone_alert,null))
            binding.title.text = context.resources.getString(R.string.geozone_alert_title) + " "+friend.name
            alertDialogBuilder.setButton(Dialog.BUTTON_POSITIVE,context.resources.getString(R.string.accept),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    observer.addedGeo()
                    dialogInterface.dismiss()
                })
            alertDialogBuilder.setButton(Dialog.BUTTON_NEGATIVE,context.resources.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })

            alertDialogBuilder.setView(binding.root)
            alertDialogBuilder.show()
            alertDialogBuilder.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.red))
            alertDialogBuilder.getButton(Dialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.blue_a7))
        }

        fun friends_geozone_delete_dialog( geozeones: ArrayList<GeofenceFriend>, context: Context, observer: UIObserverGeofenceD){
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context).setCancelable(false).create()
            val binding = GeofenceFriendAlertBinding.bind(LayoutInflater.from(context).inflate(R.layout.geofence_friend_alert,null))

            var adapter = FriendGeofenceAdapter(context = context, list =  geozeones, observer)
            binding.rvGeofences.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            binding.rvGeofences.adapter = adapter

            alertDialogBuilder.setButton(Dialog.BUTTON_NEGATIVE,context.resources.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })

            alertDialogBuilder.setView(binding.root)
            alertDialogBuilder.show()
            alertDialogBuilder.getButton(Dialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.blue_a7))
        }


        fun update_hour_location_friend(friend: Friend, context: Context, observer: UIObserverLocationFriend){
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context).setCancelable(false).create()
            val binding = HourLocationAlertBinding.bind(LayoutInflater.from(context).inflate(R.layout.hour_location_alert,null))
            binding.timeStart.setText(friend.hour_start)
            binding.timeEnd.setText(friend.hour_end)
            binding.tvtitle.setText(context.resources.getString(R.string.location_time_friend_shorta) +
                " "+friend.name + " "+ context.resources.getString(R.string.location_time_friend_shortb)
            )
            binding.timeStart.setOnClickListener {
                binding.cvtimeStart.performClick()
            }
            binding.timeEnd.setOnClickListener {
                binding.cvtimeEnd.performClick()
            }
            binding.cvtimeStart.setOnClickListener {
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        val hour = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)
                       binding.timeStart.setText(hour)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )

                timePickerDialog.show()
            }

            binding.cvtimeEnd.setOnClickListener {
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        val hour = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)
                        binding.timeEnd.setText(hour)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )

                timePickerDialog.show()
            }

            alertDialogBuilder.setButton(Dialog.BUTTON_POSITIVE,context.resources.getString(R.string.accept),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    var frien = friend
                    frien.hour_start = binding.timeStart.text.toString()
                    frien.hour_end = binding.timeEnd.text.toString()
                    if (!Utils.isHora1Greater(frien.hour_start, frien.hour_end)) {
                        observer.update(friend = frien)
                        dialogInterface.dismiss()
                    }
                    else{
                        Toast.makeText(context,context.resources.getString(R.string.error_time),Toast.LENGTH_LONG).show()
                    }
                })
            alertDialogBuilder.setButton(Dialog.BUTTON_NEGATIVE,context.resources.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })

            alertDialogBuilder.setView(binding.root)
            alertDialogBuilder.show()
            alertDialogBuilder.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.red))
            alertDialogBuilder.getButton(Dialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.blue_a7))
        }


    }
}