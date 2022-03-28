package com.tanav.eztoll.models

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