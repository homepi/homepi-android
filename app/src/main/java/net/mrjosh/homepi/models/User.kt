package net.mrjosh.homepi.models

import java.io.Serializable
import java.math.BigInteger

class User private constructor(
    val id: BigInteger,
    val username: String,
    val fullname: String,
    val avatar: String,
//    val role: Role
) : Serializable