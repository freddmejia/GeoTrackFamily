package com.example.geotrackfamily.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geotrackfamily.databinding.FriendAlertBinding
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.R
import com.example.geotrackfamily.adapter.FriendAdapter
import com.example.geotrackfamily.adapter.FriendsRequestAdapter
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.observer.UIObserverFriendRequest

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
    }
}