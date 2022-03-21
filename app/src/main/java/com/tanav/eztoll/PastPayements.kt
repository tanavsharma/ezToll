package com.tanav.eztoll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Document

class PastPayements : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentArrayList: ArrayList<PaymentInfo>
    private lateinit var myAdapter: PaymentInfoAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var user: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_payements)

        auth = Firebase.auth
        user = auth.currentUser!!.uid

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        paymentArrayList =  arrayListOf()
        myAdapter = PaymentInfoAdapter(paymentArrayList)
        recyclerView.adapter = myAdapter

        EventChangeListener()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection(user).addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ){
                if(error != null){
                    Log.e("Error",error.message.toString())
                    return
                }

                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        paymentArrayList.add(dc.document.toObject(PaymentInfo::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })

    }
}