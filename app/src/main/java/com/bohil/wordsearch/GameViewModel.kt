package com.bohil.wordsearch

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    // Word bank
    var words = arrayOf(
        "swift", "kotlin", "objectivec",
        "variable", "java", "mobile", "intern", "summer"
    )

    var gameBoard = arrayOf<Array<String>>()

    /**
     * Creates the letters inside of the table
     * First shuffles the words inside the word array
     * Then words are picked from the array and broken down into letters
     * After a random spot has been picked that has enough space for all the letters, the letters
     * are placed
     * Continues until there are no more words inside the word bank
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
