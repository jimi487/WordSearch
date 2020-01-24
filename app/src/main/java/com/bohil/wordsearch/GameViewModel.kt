package com.bohil.wordsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {

    val wordsFound = 0
    var BOARD_SIZE = 10
    var gameBoard = Array(BOARD_SIZE) { Array(BOARD_SIZE) { "-" } }
    private val _currentText = MutableLiveData<String>("")
    val currentText: LiveData<String>
        get() = _currentText
    var words = listOf(
        "swift", "kotlin", "objectivec",
        "variable", "java", "mobile", "shopify", "intern", "summer"
    )


    /**
     * Initializes the game board table
     * Loads the words from the word list into the game board
     * Currently a max words of 9 can be handled
     */
    fun loadWords(boardSize: Int = 10) {
        var rowStart = 0
        var colStart = 0
        var orientation: Int
        var spaceAvail: Boolean

        var canPlace: Boolean

        // Words are sorted so larger words are placed first
        sortWords()

        for (word in words) {
            //Toast.makeText(activity, "Current word is $word", Toast.LENGTH_LONG).show()
            canPlace = true
            spaceAvail = false

            while (canPlace) {
                // Deciding the word orientation
                // Horizontal: 0    Vertical: 1
                orientation = Random.nextInt(2)
                var placeAtmpts = 0
                when (orientation) {

                    0 -> {
                        // Finding a position on the board for the word
                        while (!spaceAvail && canPlace) {
                            spaceAvail = true
                            // Ensuring the word is within table borders
                            do {
                                colStart =
                                    if (word.length == boardSize) 0 else Random.nextInt(boardSize - word.length)
                                rowStart = Random.nextInt(boardSize)
                            } while (word.length + colStart > boardSize)

                            // Ensuring there are no other characters in the spot for the word
                            for (char in colStart until (if (colStart + word.length < boardSize) colStart + word.length else boardSize)) {
                                if (gameBoard[rowStart][char] != "-") {
                                    spaceAvail = false
                                }
                            }
                            placeAtmpts++

                            if (placeAtmpts > 5) {
                                canPlace = false
                            }
                        }
                    }

                    1 -> {
                        while (!spaceAvail && canPlace) {
                            spaceAvail = true

                            // Ensuring the word is within table borders
                            do {
                                colStart = Random.nextInt(boardSize)
                                rowStart =
                                    if (word.length == boardSize) 0 else Random.nextInt(boardSize - word.length)
                            } while (word.length + rowStart > boardSize)

                            for (char in rowStart until (if (rowStart + word.length < boardSize) rowStart + word.length else boardSize)) {
                                if (gameBoard[char][colStart] != "-") {
                                    spaceAvail = false
                                }
                            }
                            placeAtmpts++
                            if (placeAtmpts > 5) {
                                canPlace = false
                            }
                        }
                    }
                }
                if (canPlace) {
                    // Adding the word into the table
                    if (orientation == 0) {
                        var x = 0
                        while (x < word.length) {
                            gameBoard[rowStart][colStart] = word[x].toString()
                            x++
                            colStart++
                        }
                    } else {
                        var x = 0
                        while (x < word.length) {
                            gameBoard[rowStart][colStart] = word[x].toString()
                            x++
                            rowStart++
                        }
                    }
                    // Updating loop condition
                    canPlace = false
                    // If unable to place word, reset canPlace to true so the loop continues again
                } else {
                    canPlace = true
                }
            }
        }
    }

    /**
     * Fills the rest of the table with random characters
     */
    fun fillTable() {
        // Rest of table filled with random characters
        val ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz"
        for (row in 0 until BOARD_SIZE)
            for (col in 0 until BOARD_SIZE) {
                if (gameBoard[row][col] == "-")
                    gameBoard[row][col] =
                        ALLOWED_CHARACTERS[Random.nextInt(ALLOWED_CHARACTERS.length)].toString()
            }

    }

    /**
     * Refreshes the game board to new game
     */
    fun refreshBoard() {
        gameBoard = Array(BOARD_SIZE) { Array(BOARD_SIZE) { "-" } }
        _currentText.value = ""
    }

    /**
     * Adds the tapped character button to the current guess selection
     */
    fun addToSelection(char: String) {

        //TODO Determine if its the first button pressed

        //TODO Ensure the next character press is next to the previous character press

        // TODO Determine the orientation after the next character presses

        //
        _currentText.value += char
    }

    //TODO Remove the last character in the current text selection
    fun removeFromSelection() {

    }

    // TODO Clear the current selection string
    fun clearCurrentSelection() {

    }

    // TODO Submits the word and checks if its valid
    fun submitWord() {
        _currentText.value = ""
        for (word in words) {
            if (_currentText.value in words) {

            }
        }


    }

    fun findInList() {

    }

    /**
     * Sorts words list from largest to smallest
     */
    private fun sortWords() {
        //Words are sorted so larger words are placed first
        words = words.sortedWith(Comparator<String> { s1, s2 ->
            when {
                s1.length > s2.length -> -1
                s1.length < s2.length -> 1
                else -> 0
            }
        })
    }
}
