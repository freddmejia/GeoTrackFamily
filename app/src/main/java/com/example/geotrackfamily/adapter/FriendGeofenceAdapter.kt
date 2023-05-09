package com.example.geotrackfamily.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.GeofenceFriendItemBinding
import com.example.geotrackfamily.models.GeofenceFriend
import com.example.geotrackfamily.observer.UIObserverGeofenceD

class FriendGeofenceAdapter(val context: Context, var list: List<GeofenceFriend>, val observer: UIObserverGeofenceD):
    RecyclerView.Adapter<FriendGeofenceAdapter.holderBrandAdapter>() {

    class holderBrandAdapter(binding: GeofenceFriendItemBinding): RecyclerView.ViewHolder(binding.root){
        private val binding = binding
        fun binData(shortUser: GeofenceFriend, observer: UIObserverGeofenceD){
            binding.tvZone.text = shortUser.zone
            binding.tvlat.text = shortUser.latitude
            binding.tvlong.text = shortUser.longitude
            binding.deleteGeofence.setOnClickListener{
                observer.deleteGeofence(geofence = shortUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holderBrandAdapter {
        val binding = GeofenceFriendItemBinding.bind(LayoutInflater.from(context).inflate(R.layout.geofence_friend_item,parent, false))
        return holderBrandAdapter(
            binding = binding
        )
    }

    override fun onBindViewHolder(holder: holderBrandAdapter, position: Int) {
        holder.binData(shortUser = list[position], observer = observer)
    }

    override fun getItemCount() = list.size

    fun setNewData(arrayList: List<GeofenceFriend>) {
        list = arrayList
        notifyDataSetChanged()
    }

    /*fun deleteUser(id: Int) {
        val tempList : List<Friend> = arrayListOf()
        list.
        notifyDataSetChanged()
    }*/
}