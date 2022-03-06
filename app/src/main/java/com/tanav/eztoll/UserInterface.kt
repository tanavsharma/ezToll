package com.tanav.eztoll

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tanav.eztoll.Interfaces.ImageListener
import com.tanav.eztoll.Interfaces.ImageUploadListener
import com.tanav.eztoll.Models.User
import com.tanav.eztoll.fragments.PaymentHistoryFragment
import com.tanav.eztoll.fragments.TollMapFragment
import com.tanav.eztoll.fragments.TrackMapFragment
import com.tanav.eztoll.fragments.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class UserInterface : AppCompatActivity() {


    private val mOnNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.userFragment -> switchFragment(UserFragment())
                R.id.tollMapFragment -> switchFragment(TollMapFragment())
                R.id.trackMapFragment -> switchFragment(TrackMapFragment())
                R.id.paymentHistoryFragment -> switchFragment(PaymentHistoryFragment())
            }
            false
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_interface)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        bottomNavigationView.setOnItemSelectedListener(mOnNavigationItemSelectedListener)

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        // add the default fragment
        transaction.add(R.id.host_fragment_content_user_interface, UserFragment())
        transaction.commit()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // create a fragment instance according to the option menu selection
        return if (item.itemId == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun switchFragment(toFragment: Fragment) : Boolean {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        //replace fragment based on the parameter
        transaction.replace(R.id.host_fragment_content_user_interface, toFragment)
        //transaction.replace(R.id.nav_fragment, toFragment)
        transaction.commit()

        return true
    }
}
