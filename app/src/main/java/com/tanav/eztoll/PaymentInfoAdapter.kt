package com.tanav.eztoll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class PaymentInfoAdapter(private val paymentList : ArrayList<PaymentInfo>) : RecyclerView.Adapter<PaymentInfoAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentInfoAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentInfoAdapter.MyViewHolder, position: Int) {
        val payment : PaymentInfo = paymentList[position]
        holder.amount.text = payment.amount.toString()
        holder.dateOfPayment.text = payment.dateOfPayment.toString()

    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val amount : TextView = itemView.findViewById(R.id.paymentAmount)
        val dateOfPayment : TextView = itemView.findViewById(R.id.dateOfPayment)

    }

}