package com.example.geotrackfamily.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.geotrackfamily.MainActivity
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.FriendFragmentBinding
import com.example.geotrackfamily.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendFragment : Fragment(R.layout.friend_fragment) {
    private var binding: FriendFragmentBinding? = null

    companion object{
        fun newInstance(): FriendFragment {
            return FriendFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val friendFragmenBinding = FriendFragmentBinding.bind(view)
        binding = friendFragmenBinding!!
        events()

    }

    fun events(){

    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}