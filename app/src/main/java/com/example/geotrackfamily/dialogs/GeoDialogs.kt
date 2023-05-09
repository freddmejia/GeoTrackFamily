package com.example.geotrackfamily.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.example.geotrackfamily.databinding.FriendAlertBinding
import com.example.geotrackfamily.R
import com.example.geotrackfamily.adapter.FriendGeofenceAdapter
import com.example.geotrackfamily.adapter.FriendsRequestAdapter
import com.example.geotrackfamily.databinding.AddGeozoneAlertBinding
import com.example.geotrackfamily.databinding.GeofenceFriendAlertBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.GeofenceFriend
import com.example.geotrackfamily.observer.UIObserverFriendGeoZone
import com.example.geotrackfamily.observer.UIObserverFriendRequest
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.observer.UIObserverGeofenceD

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

    }
}