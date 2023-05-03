package com.example.geotrackfamily

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.geotrackfamily.databinding.ActivityMainAppBinding
import com.example.geotrackfamily.databinding.BottomBarBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.fragment.FriendFragment
import com.example.geotrackfamily.fragment.HomeFragment
import com.example.geotrackfamily.fragment.ZoneFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainAppActivity : AppCompatActivity() {
    val TAG = "MainAppActivity"
    private lateinit var binding: ActivityMainAppBinding
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    private lateinit var bottomBarBinding: BottomBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarAppBinding = ToolbarAppBinding.bind(binding.root)
        bottomBarBinding = BottomBarBinding.bind(binding.root)
        setSupportActionBar(toolbarAppBinding.toolbar)

        events()
        chooseSelectionMenu(fragment = HomeFragment.newInstance())
    }

    override fun onBackPressed() {
        try {
            val currentFragment: Fragment? =
                supportFragmentManager.fragments.last()
            Log.e(TAG, "onResume: "+currentFragment?.toString() )

        }catch (e: java.lang.Exception){
            Log.e(TAG, "Exception: "+e.message )
        }
        super.onBackPressed()

    }

    fun events() {
        toolbarAppBinding.profile.setOnClickListener {
            startActivity(
                Intent(this@MainAppActivity, ProfileActivity::class.java)
            )
        }

        bottomBarBinding.rvHome.setOnClickListener {
            chooseSelectionMenu(fragment = HomeFragment.newInstance())
        }
        bottomBarBinding.rvFriend.setOnClickListener {
            chooseSelectionMenu(fragment = FriendFragment.newInstance())
        }
        bottomBarBinding.rvZone.setOnClickListener {
            chooseSelectionMenu(fragment = ZoneFragment.newInstance())
        }

    }

    fun chooseSelectionMenu(fragment: Fragment){


        when(fragment){
            is HomeFragment -> {
                appeareance(
                    cardView = bottomBarBinding.cvHome,
                    imageView = bottomBarBinding.imHome,
                    colorCv = R.color.green_b1,
                    colorIm = R.color.white
                )

                appeareance(
                    cardView = bottomBarBinding.cvFriend,
                    imageView = bottomBarBinding.imFriend,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

                appeareance(
                    cardView = bottomBarBinding.cvZone,
                    imageView = bottomBarBinding.imZone,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )
                //bottomBarBinding.cvHome.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green_b1))
                //bottomBarBinding.imHome.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)

            }
            is FriendFragment -> {
                appeareance(
                    cardView = bottomBarBinding.cvFriend,
                    imageView = bottomBarBinding.imFriend,
                    colorCv = R.color.green_b1,
                    colorIm = R.color.white
                )

                appeareance(
                    cardView = bottomBarBinding.cvHome,
                    imageView = bottomBarBinding.imHome,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

                appeareance(
                    cardView = bottomBarBinding.cvZone,
                    imageView = bottomBarBinding.imZone,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

            }
            is ZoneFragment -> {
                appeareance(
                    cardView = bottomBarBinding.cvZone,
                    imageView = bottomBarBinding.imZone,
                    colorCv = R.color.green_b1,
                    colorIm = R.color.white
                )

                appeareance(
                    cardView = bottomBarBinding.cvHome,
                    imageView = bottomBarBinding.imHome,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

                appeareance(
                    cardView = bottomBarBinding.cvFriend,
                    imageView = bottomBarBinding.imFriend,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )
                //bottomBarBinding.cvZone.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green_b1))
                //bottomBarBinding.imZone.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)

            }
        }
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out)
            replace(R.id.frameContainer,fragment)
            setReorderingAllowed(true)
        }
    }

    fun appeareance(cardView: CardView, imageView: ImageView, colorCv: Int, colorIm: Int){
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, colorCv))
        imageView.setColorFilter(ContextCompat.getColor(this, colorIm), PorterDuff.Mode.SRC_IN)
    }

}