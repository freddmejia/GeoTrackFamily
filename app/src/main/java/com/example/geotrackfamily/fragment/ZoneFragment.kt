package com.example.geotrackfamily.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.FriendFragmentBinding
import com.example.geotrackfamily.databinding.ZoneFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ZoneFragment : Fragment(R.layout.zone_fragment) {
    private var binding: ZoneFragmentBinding? = null

    companion object{
        fun newInstance(): ZoneFragment {
            return ZoneFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val zoneFragmentBinding = ZoneFragmentBinding.bind(view)
        binding = zoneFragmentBinding!!
        events()

    }

    fun events(){

    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}