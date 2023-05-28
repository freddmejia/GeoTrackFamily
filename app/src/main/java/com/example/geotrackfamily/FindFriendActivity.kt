package com.example.geotrackfamily

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geotrackfamily.adapter.FriendAdapter
import com.example.geotrackfamily.databinding.ActivityFindFriendBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.FriendRequest
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.utility.Result
import com.example.geotrackfamily.utility.Utils
import com.example.geotrackfamily.viewModels.FriendViewModel
import dagger.hilt.android.AndroidEntryPoint
import okio.Utf8
import org.json.JSONObject

@AndroidEntryPoint
class FindFriendActivity : AppCompatActivity(), UIObserverGeneric<Friend> {
    private lateinit var binding: ActivityFindFriendBinding
    private val friendViewModel: FriendViewModel by viewModels()
    private lateinit var friendAdapter: FriendAdapter
    private var friendsList = arrayListOf<Friend>()
    private lateinit var user: User
    private lateinit var toast: Toast
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    private var isAddedRequest = 0
    val TAG = "FindFriendActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolBar()
        friendViewModel.fetch_possible_friends()

        friendAdapter = FriendAdapter(this@FindFriendActivity, list = arrayListOf(), this )
        binding.rvFriends.layoutManager = GridLayoutManager(this@FindFriendActivity, 2, GridLayoutManager.VERTICAL, false)
        binding.rvFriends.adapter = friendAdapter
        toast = Toast(this)

        val prefsUser = this@FindFriendActivity.getSharedPreferences(
            resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        user = User(JSONObject(prefsUser!!.getString("user","")))

        events()
        coroutines()
    }

    fun events(){
        binding.etNameEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                Log.e(TAG, "onTextChanged1: "+inputText )
                if (inputText.trim().length >= 2){
                    Log.e(TAG, "onTextChanged2: " )
                    friendViewModel.setInputText(inputText = inputText)
                }
                if (inputText.trim().length <= 0) {
                    friendsList.clear()
                    friendAdapter.setNewData(friendsList)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Este método se llama después de que el texto cambie
            }
        })
    }

    fun coroutines(){

        lifecycleScope.launchWhenCreated {
            friendViewModel.loadingProgress.collect {
                binding.rvFriends.isVisible = !it
                binding.progressBar.isVisible = it
            }
        }

        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionFetchPossibleFriends.collect { result->
                when(result){
                    is Result.Success<CompositionObj<ArrayList<Friend>, String>> ->{
                        friendsList.clear()
                        friendsList.addAll(result.data.data)
                        friendAdapter.setNewData(friendsList)
                        closeKeyBoard()

                    }
                    is Result.Error ->{
                        showToast(message = result.error)
                        friendsList.clear()
                        friendAdapter.setNewData(friendsList)
                    }


                    else -> Unit
                }

            }
        }

        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionFriendRequest.collect { result->
                when(result){
                    is Result.Success<CompositionObj<FriendRequest, String>> ->{

                        var friend = friendsList.find { friend -> friend.id  == result.data.data.user_id2 }
                        val position = friendsList.indexOfFirst { friend -> friend.id  == result.data.data.user_id2 }

                        if (friend != null) {
                            friend?.is_friend = if (isAddedRequest == Utils.friend_added) Utils.friend_added else Utils.friend_cancel
                            friendsList.remove(friend)
                            //friendsList.add(friend)
                            friendsList.add(position, friend)
                        }

                        friendAdapter.setNewData(friendsList)
                        showToast(message = result.data.message)
                    }
                    is Result.Error ->
                        showToast(message = result.error)

                    else -> Unit
                }
            }
        }
    }

    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@FindFriendActivity,message, Toast.LENGTH_LONG)
        toast.show()
    }

    fun closeKeyBoard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

    }

    fun setUpToolBar() {
        toolbarAppBinding = ToolbarAppBinding.bind(binding.root)
        setSupportActionBar(toolbarAppBinding.toolbar)
        toolbarAppBinding.titleBar.text = resources.getString(R.string.find_friend)
        toolbarAppBinding.profile.isVisible = false
        toolbarAppBinding.rvNotification.isVisible = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->
            {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onOkButton(data: Friend) {
        Log.e(TAG, "onOkButton: create "+data.id.toString() )
        isAddedRequest = Utils.friend_added
        friendViewModel.friend_request(user_id1 = user.id.toString(), user_id2 = data.id.toString())
    }

    override fun onCancelButton(data: Friend) {
        Log.e(TAG, "onCancelButton: cancel" )
        isAddedRequest = Utils.are_friend
        friendViewModel.delete_friend_request(user_id1 = user.id.toString(), user_id2 = data.id.toString())
    }
}