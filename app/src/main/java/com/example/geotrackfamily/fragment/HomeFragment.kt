package com.example.geotrackfamily.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.geotrackfamily.FindFriendActivity
import com.example.geotrackfamily.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.HomeFragmentBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {
    private var binding: HomeFragmentBinding ? = null

    companion object{
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeFragmentBinding = HomeFragmentBinding.bind(view)
        binding = homeFragmentBinding!!
        events()

    }

    fun events(){
        binding?.addFriend?.setOnClickListener {
            startActivity(Intent(this@HomeFragment.context, FindFriendActivity::class.java))
        }
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}