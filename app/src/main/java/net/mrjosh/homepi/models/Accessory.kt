package net.mrjosh.homepi.models

import java.math.BigInteger

class Accessory private constructor (
    val id: BigInteger,
//    val pin_id: Int,
//    val pin_type: Int,
//    val task: Int,
//    val is_active: Boolean,
//    val is_public: Boolean,
    val name: String,
    val description: String,
    val icon: String
)
