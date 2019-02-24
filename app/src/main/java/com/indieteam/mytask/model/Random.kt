package com.indieteam.mytask.model

import kotlin.random.Random

class Random {

    companion object {
        fun get_string(outputLength: Int): String {
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            var result = ""
            for (i in 0 until outputLength) {
                val intRandom = Random.nextInt(0, source.length)
                result += source[intRandom].toString()
            }
            return result
        }
    }
}