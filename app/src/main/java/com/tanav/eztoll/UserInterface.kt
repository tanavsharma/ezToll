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
import kotlinx.android.synthetic.main.activity_main.*

class UserInterface : AppCompatActivity() {

    private lateinit var nameOfUser_TV: TextView
    private lateinit var profilePic_IV: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var first_Name : String
    private lateinit var last_Name : String
    private lateinit var gender_ : String
    private lateinit var address_Street: String
    private lateinit var address_Country : String
    private lateinit var address_City : String
    private lateinit var address_postalCode : String

    var imgeUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_interface)

        nameOfUser_TV = findViewById(R.id.NameOfUser)
        profilePic_IV = findViewById(R.id.profile_image)

        auth = Firebase.auth

        database = Firebase.database.getReference("Users")
        val userUniqueID = auth.currentUser!!.uid
        Log.d("TAG", "message" + userUniqueID)

        database.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val user = snapshot.getValue(User::class.java)
                    nameOfUser_TV.text = user!!.firstName
                    val profileImageURl = user.image
                    if(profileImageURl.equals("no pic")){
                        Toast.makeText(applicationContext, "No Profile Picture", Toast.LENGTH_SHORT).show()
                    }else{
                        Picasso.get().load(profileImageURl).into(profilePic_IV)
                    }


                    first_Name = user!!.firstName
                    last_Name = user!!.lastName
                    gender_ = user!!.gender
                    address_Street = user!!.streetName
                    address_Country = user!!.streetCountry
                    address_City = user!!.streetCity
                    address_postalCode = user!!.postalCode


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        profilePic_IV.setOnClickListener {
            Util.readImageFromGallery(this@UserInterface, object : ImageListener {
                override fun onImageLoaded(error: Boolean, uri: Uri?, bitmap: Bitmap?) {

                    if (!error) {

                        imgeUri = uri!!
                        profilePic_IV.setImageURI(uri)

                        Util.uploadImage(imgeUri,object:ImageUploadListener{
                            override fun onUpload(error: Boolean, Message: String?, url: String?) {
                                val userValues = User(first_Name, last_Name, gender_, address_Street, address_Country, address_City, address_postalCode,url)
                                database.child(userUniqueID!!).setValue(userValues)
                            }

                        })

                    } else {

                    }
                }
            })


        }



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

}
