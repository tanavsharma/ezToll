package com.tanav.eztoll.Models

import android.net.Uri

data class User(val firstName:String,
                val lastName:String,
                val gender:String,
                val streetName: String,
                val streetCountry: String,
                val streetCity: String,
                val postalCode: String,
                var image: String?
)

{


    constructor() : this("","","","","","","", "")

}