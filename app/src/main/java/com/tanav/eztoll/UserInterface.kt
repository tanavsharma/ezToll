package com.tanav.eztoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.tanav.eztoll.fragments.PaymentHistoryFragment
import com.tanav.eztoll.fragments.TollMapFragment
import com.tanav.eztoll.fragments.TrackMapFragment
import com.tanav.eztoll.fragments.UserFragment

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
        
        return if(item.itemId == R.id.action_settings){
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }else if(item.itemId == R.id.update_profile){
            val intent = Intent(this, UserInformation::class.java)
            startActivity(intent)
            true
        }else if(item.itemId == R.id.paymentScreen){
            val intent = Intent(this, PaymentHistory::class.java)
            startActivity(intent)
            true
        }else{
            super.onOptionsItemSelected(item)
        }


//        return if (item.itemId == R.id.update_profile) {
//            val intent2 = Intent(this, UserInformation::class.java)
//            startActivity(intent2)
//            true
//        } else {
//            super.onOptionsItemSelected(item)
//        }
//
//        return if (item.itemId == R.id.paymentScreen) {
//            val intent3 = Intent(this, PaymentHistory::class.java)
//            startActivity(intent3)
//            true
//        } else {
//            super.onOptionsItemSelected(item)
//        }

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
