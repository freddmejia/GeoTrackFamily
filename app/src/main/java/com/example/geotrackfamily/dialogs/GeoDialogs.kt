package com.example.geotrackfamily.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geotrackfamily.databinding.FriendAlertBinding
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.R
import com.example.geotrackfamily.adapter.FriendAdapter

class GeoDialogs {
    companion object {
        /*fun brand_wearable(context: Context, observer: UIObserverGeneric<shortUser>){
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context).setCancelable(false).create()
            val binding = FriendAlertBinding.bind(LayoutInflater.from(context).inflate(R.layout.friend_alert,null))

            var adapter = FriendAdapter(context = context, list =  arrayListOf(), observer = observer, alertDialogBuilder = alertDialogBuilder)
            binding.rvFriends.layoutManager = LinearLayoutManager(context)

            binding.rvFriends.adapter = adapter

            alertDialogBuilder.setView(binding.root)
            alertDialogBuilder.show()

        }*/
    }
}