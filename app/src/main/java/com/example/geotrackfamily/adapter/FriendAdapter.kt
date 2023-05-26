package com.example.geotrackfamily.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.geotrackfamily.databinding.FindFriendItemBinding
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.R
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.utility.Utils

class FriendAdapter (val context: Context, var list: List<Friend>, val observer: UIObserverGeneric<Friend>):
    RecyclerView.Adapter<FriendAdapter.holderBrandAdapter>() {

    class holderBrandAdapter(binding: FindFriendItemBinding): RecyclerView.ViewHolder(binding.root){
        private val binding = binding
        val TAG = "FriendAdapter"
        fun binData(shortUser: Friend, observer: UIObserverGeneric<Friend>){
            Log.e(TAG, "binData: "+shortUser.name + " "+shortUser.email )
            binding.nameUser.text = shortUser.name
            binding.addUser.setOnClickListener{
                observer.onOkButton(data = shortUser)
            }
            binding.deleteFriend.setOnClickListener {
                observer.onCancelButton(data = shortUser)
            }

            binding.isFriend.isVisible = shortUser.is_friend == Utils.friend_added
            binding.addUser.isVisible = !(shortUser.is_friend == Utils.are_friend)
            binding.deleteFriend.isVisible = shortUser.is_friend == Utils.are_friend
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderBrandAdapter {
        val binding = FindFriendItemBinding.bind(LayoutInflater.from(context).inflate(R.layout.find_friend_item,parent, false))
        return holderBrandAdapter(
            binding = binding
        )
    }

    override fun onBindViewHolder(holder: holderBrandAdapter, position: Int) {
        holder.binData(shortUser = list[position], observer = observer)
    }

    override fun getItemCount() = list.size

    fun setNewData(arrayList: List<Friend>) {
        list = arrayList
        notifyDataSetChanged()
    }

}