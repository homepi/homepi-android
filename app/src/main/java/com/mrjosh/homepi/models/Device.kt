package com.mrjosh.homepi.models

import java.math.BigInteger

class Device private constructor(
    val id: BigInteger,
    val type: String,
    val os: String,
    val model: String,
    val icon: String
)