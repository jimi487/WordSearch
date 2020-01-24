package com.bohil.wordsearch

import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random


//TODO Currently _currentText and the selected buttons list are separate entities
class GameViewModel : ViewModel() {

    var wordsFound = 0
    var BOARD_SIZE = 10
    var gameBoard = Array(BOARD_SIZE) { Array(BOARD_SIZE) { "-" } }
    var words = listOf(
        "swift", "kotlin", "objectivec",
        "variable", "java", "mobile", "shopify", "intern", "summer"
    )

    private val _currentText = MutableLiveData<String>("")
    val currentText: LiveData<String>
        get() = _currentText

    private class SelectedButton(val char: String, val location: Pair<Int, Int>)

    private var selectedButtonsList = ArrayList<SelectedButton>()


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
        wordsFound = 0
        clearSelection()
    }


    /**
     * Adds the tapped character button to the current text selection
     * Determines the orientation of the users current selection
     * Restricts the user from selecting characters outside of the current orientation and path
     *
     * Returns:
     * False - The word was not added to the current selection
     * True - The word was added
     */
    fun addToSelection(btn: Button): Boolean {
        // Create object for button
        val xyLocation = btn.tag.toString().takeLast(2)
        val crntBtn =
            SelectedButton(
                btn.text.toString(),
                Pair(xyLocation[0].toString().toInt(), xyLocation[1].toString().toInt())
            )

        /**
         * Determines whether given buttons are neighbors
         */
        fun isNeighbor(btnPrev: Pair<Int, Int>, btnCurrent: Pair<Int, Int>): Boolean {
            return (((btnPrev.first == btnCurrent.first) && (btnPrev.second == btnCurrent.second - 1))
                    ||
                    ((btnPrev.first == btnCurrent.first - 1) && (btnPrev.second == btnCurrent.second)))

        }

        /**
         * Determines whether the selected word is in the same orientation
         */
        fun sameOrientation(
            btnPprev: Pair<Int, Int>,
            btnPrev: Pair<Int, Int>,
            btnCurrent: Pair<Int, Int>
        ): Boolean {
            return (((btnPprev.first == btnPrev.first - 1) && (btnPrev.first == btnCurrent.first - 1))
                    ||
                    ((btnPprev.second == btnPrev.second - 1) && (btnPrev.second == btnCurrent.second - 1)))

        }


        when (selectedButtonsList.size) {
            // When the string is empty, add it to the selection
            0 -> {
                _currentText.value += crntBtn.char
                selectedButtonsList.add(crntBtn)
                return true
            }
            1 -> {
                // Determine if the current selection is next to the previous selection
                val prevBtn = selectedButtonsList[0]
                if (isNeighbor(prevBtn.location, crntBtn.location)) {
                    _currentText.value += crntBtn.char
                    selectedButtonsList.add(crntBtn)
                    return true
                } else return false
            }
            in 2 until BOARD_SIZE -> {
                val pprevBtn = selectedButtonsList.get(selectedButtonsList.size - 2)
                val prevBtn = selectedButtonsList.get(selectedButtonsList.size - 1)

                if (isNeighbor(prevBtn.location, crntBtn.location)
                    && sameOrientation(pprevBtn.location, prevBtn.location, crntBtn.location)
                ) {
                    _currentText.value += crntBtn.char
                    selectedButtonsList.add(crntBtn)
                    return true
                } else return false
            }
            else -> {
                return false
            }
        }

    }

    /**
     * Removes the last added character from the current selection
     */
    fun removeFromSelection() {
        selectedButtonsList.removeAt(selectedButtonsList.size - 1)
        _currentText.value =
            _currentText.value.toString().substring(0, _currentText.value.toString().length - 1)
    }

    /**
     * Clears the current text selection
     */
    fun clearSelection() {
        do {
            selectedButtonsList.removeAt(0)
        } while (selectedButtonsList.size > 0)
        _currentText.value = ""
    }


    /**
     * Submits the word and checks if its valid
     * Clearing of the text is done inside the GameFragment
     */
    fun submitWord(): Boolean {
        if (_currentText.value in words) {
            wordsFound++
            return true
        }
        return false
    }

    /**
     * Returns a list of all the pairs for each button in the selected button list
     */
    fun getSelectedPairs(): ArrayList<Pair<Int, Int>> {
        val pairs = ArrayList<Pair<Int, Int>>()
        for (pair in selectedButtonsList)
            pairs.add(Pair(pair.location.first, pair.location.second))
        return pairs
    }

    /**
     * Sorts words list from largest to smallest
     * Larger words are placed first
     */
    private fun sortWords() {
        words = words.sortedWith(Comparator<String> { s1, s2 ->
            when {
                s1.length > s2.length -> -1
                s1.length < s2.length -> 1
                else -> 0
            }
        })
    }
}
