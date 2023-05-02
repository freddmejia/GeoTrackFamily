package com.example.geotrackfamily.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.FindFriendItemBinding
import com.example.geotrackfamily.databinding.HomeFriendItemBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.utility.Utils

class FriendsAdapter (val context: Context, var list: List<Friend>, val observer: UIObserverGeneric<Friend>):
    RecyclerView.Adapter<FriendsAdapter.HolderFriend>() {

    class HolderFriend(binding: HomeFriendItemBinding): RecyclerView.ViewHolder(binding.root){
        private val binding = binding
        val TAG = "FriendsAdapter"
        fun binData(shortUser: Friend, observer: UIObserverGeneric<Friend>){
            Log.e(TAG, "binData: "+shortUser.name + " "+shortUser.email )
            binding.nameUser.text = shortUser.name
            binding.editUser.setOnClickListener{
                observer.onOkButton(data = shortUser)
            }
            binding.deleteUser.setOnClickListener {
                observer.onCancelButton(data = shortUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFriend {
        val binding = HomeFriendItemBinding.bind(LayoutInflater.from(context).inflate(R.layout.home_friend_item,parent, false))
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

    /*fun deleteUser(id: Int) {
        val tempList : List<Friend> = arrayListOf()
        list.
        notifyDataSetChanged()
    }*/
}