package net.mrjosh.homepi.models

import java.math.BigInteger

class Log private constructor(
    val id: BigInteger,
    val user: User,
    val type: Int,
    val accessory: Accessory,
    val created_at: String
)