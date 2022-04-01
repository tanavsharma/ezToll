package com.tanav.eztoll.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.tanav.eztoll.PaymentInfo
import com.tanav.eztoll.PaymentInfoAdapter
import com.tanav.eztoll.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PaymentHistoryFragment : Fragment() {
    private lateinit var myContext: Context
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentArrayList: ArrayList<PaymentInfo>
    private lateinit var myAdapter: PaymentInfoAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = Firebase.auth
        user = auth.currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireActivity().applicationContext
        val myView = inflater.inflate(R.layout.fragment_payment_history, container, false)

        recyclerView = myView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)

        paymentArrayList =  arrayListOf()
        myAdapter = PaymentInfoAdapter(paymentArrayList)
        recyclerView.adapter = myAdapter

        eventChangeListener()

        return myView
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection(user).addSnapshotListener(object : EventListener<QuerySnapshot> {
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