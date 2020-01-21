package com.bohil.wordsearch

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    // Word bank
    var words = arrayOf(
        "swift", "kotlin", "objectivec",
        "variable", "java", "mobile", "shopify", "intern", "summer"
    )

    var BOARD_SIZE = 10
    var gameBoard = Array(BOARD_SIZE) { Array(BOARD_SIZE) { "-" } }

    /**
     * Shuffles the words inside the word array
     */
    fun randomizeWords() {
        for (i in words.indices) {
            val randIntToSwap = Random.nextInt(words.size)
            val temp = words[randIntToSwap]
            words[randIntToSwap] = words[i]
            words[i] = temp
        }

    }

}
