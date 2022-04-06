package com.tanav.eztoll

data class PaymentInfo(var amount: String ?= null, var dateOfPayment: String ?= null){
    override fun toString(): String {
        return "amount:$amount, dateOfPayment:$dateOfPayment"
    }
}
