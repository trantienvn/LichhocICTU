package com.indieteam.mytask

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun stringRandom(){
        val outputLength = 40
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        var result = ""
        for (i in 0 until outputLength){
            val intRandom = Random.nextInt(0, source.length)
            result += source[intRandom].toString()
        }
        print(result)
    }
}
