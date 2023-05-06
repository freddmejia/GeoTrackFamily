package com.example.geotrackfamily.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.FriendRequestItemBinding
import com.example.geotrackfamily.databinding.FriendZoneItemBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.observer.UIObserverFriendRequest
import com.example.geotrackfamily.observer.UIObserverGeneric

class FriendsZoneAdapter (val context: Context, var list: List<Friend>, val observer: UIObserverGeneric<Friend>,
                          ):
    RecyclerView.Adapter<FriendsZoneAdapter.HolderFriend>() {

    class HolderFriend(binding: FriendZoneItemBinding): RecyclerView.ViewHolder(binding.root){
        private val binding = binding
        val TAG = "FriendsZoneAdapter"
        fun binData(shortUser: Friend, observer: UIObserverGeneric<Friend>){
            binding.nameUser.text = shortUser.name
            binding.linearItem.setOnClickListener {
                observer.onOkButton(data = shortUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFriend {
        val binding = FriendZoneItemBinding.bind(LayoutInflater.from(context).inflate(R.layout.friend_zone_item,parent, false))
        return HolderFriend(
            binding = binding
        )
    }

    override fun onBindViewHolder(holder: HolderFriend, position: Int) {
        holder.binData(shortUser = list[position], observer = observer)

    }

    override fun getItemCount() = list.size

    fun setNewData(arrayList: List<Friend>) {
        list = arrayList
        notifyDataSetChanged()
    }
}