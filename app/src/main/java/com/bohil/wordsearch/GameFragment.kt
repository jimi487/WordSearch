package com.bohil.wordsearch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bohil.wordsearch.databinding.FragmentGameBinding
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.random.Random


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: FragmentGameBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        //Toast.makeText(activity, "character is: $BOARD_LENGTH", Toast.LENGTH_LONG).show()
        // Game Board Initialization
        randomizeWords()
        loadWords()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.lifecycleOwner = this
        initializeTable()
        initializeWordHints()

        return binding.root
    }

    /**
     * Randomizes the words in the word list
     */
    private fun randomizeWords() {
        viewModel.randomizeWords()
    }

    /**
     * Loads the words into the game board
     * Words in the word list are gone through in order
     */
    private fun loadWords(boardSize: Int = 10) {
        var rowStart = 0
        var colStart = 0
        var orientation: Int
        var spaceAvail: Boolean
        var exceedsBorder = false

        for (word in viewModel.words) {
            //Toast.makeText(activity, "Current word is $word", Toast.LENGTH_LONG).show()
            // Deciding the word orientation
            // Horizontal: 0    Vertical: 1
            orientation = Random.nextInt(2)
            spaceAvail = false
            // Finding a position on the board for the word
            when (orientation) {
                0 -> {
                    while (!spaceAvail) {
                        spaceAvail = true

                        // Ensuring the word is within table borders
                        do {
                            colStart =
                                if (word.length == boardSize) 0 else Random.nextInt(boardSize)
                            rowStart = Random.nextInt(boardSize)
                        } while (word.length + colStart > boardSize)

                        // Ensuring there are no other characters in the spot for the word
                        for (char in colStart until (if (rowStart + word.length < boardSize) colStart + word.length else boardSize)) {
                            if (viewModel.gameBoard[rowStart][char] != "-") {
                                spaceAvail = false
                            }
                        }
                    }
                    // Placing the word
                        var x = 0
                        while (x < word.length) {
                            viewModel.gameBoard[rowStart][colStart] = word[x].toString()
                            x++
                            colStart++
                        }


/*                    do {
                        spaceAvail = true
                        colStart = if (word.length == boardSize) 0 else Random.nextInt(boardSize)
                        rowStart = Random.nextInt(boardSize)

                        // If the position of the word exceeds array bounds, use board size instead
                        for (char in colStart until (if (colStart + word.length < boardSize) colStart + word.length else boardSize)) {
                            if (viewModel.gameBoard[rowStart][char] != "-") {
                                spaceAvail = false
                            }
                        }
                    } while (word.length + colStart > boardSize || !spaceAvail)

                    // Placing word into game board
                    var x = 0
                    while (x < word.length) {
                        viewModel.gameBoard[rowStart][colStart] = word[x].toString()
                        x++
                        colStart++
                    }*/
                }

                1 -> {
                    while (!spaceAvail) {
                        spaceAvail = true

                        // Ensuring the word is within table borders
                        do {
                            colStart = Random.nextInt(boardSize)
                            rowStart =
                                if (word.length == boardSize) 0 else Random.nextInt(boardSize)
                        } while (word.length + rowStart > boardSize)

                        for (char in rowStart until (if (rowStart + word.length < boardSize) rowStart + word.length else boardSize)) {
                            if (viewModel.gameBoard[char][colStart] != "-") {
                                spaceAvail = false
                            }
                        }
                    }

                        var x = 0
                        while (x < word.length) {
                            viewModel.gameBoard[rowStart][colStart] = word[x].toString()
                            x++
                            rowStart++
                        }


                    /*
                                       do {
                                           spaceAvail = true
                                           colStart = Random.nextInt(boardSize)
                                           rowStart = if (word.length == boardSize) 0 else Random.nextInt(boardSize)

                                           for (char in rowStart until (if (rowStart + word.length < boardSize) rowStart + word.length else boardSize)) {
                                               if (viewModel.gameBoard[char][colStart] != "-") {
                                                   spaceAvail = false
                                               }
                                           }
                                       } while (word.length + rowStart > boardSize || !spaceAvail)

                                       var x = 0
                                       while (x < word.length) {
                                           viewModel.gameBoard[rowStart][colStart] = word[x].toString()
                                           x++
                                           rowStart++
                                       }*/
                }
            }
        }
    }

    /**
     * Refreshes the game board to display the updated character table
     */
    private fun initializeTable(boardSize: Int = 10) {
        // Rest of table filled with random characters
        val ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz"
        for (row in 0 until boardSize)
            for (col in 0 until boardSize) {
                if (viewModel.gameBoard[row][col] == "-")
                    viewModel.gameBoard[row][col] =
                        ALLOWED_CHARACTERS[Random.nextInt(ALLOWED_CHARACTERS.length)].toString()
            }

        // Setting the text for each button
        for (i in 0 until boardSize) {
            val child = binding.gameGrid.getChildAt(i)
            if (child is TableRow) {
                val currentRow: TableRow = child
                for (j in 0 until boardSize) {
                    val character = currentRow.getChildAt(j)
                    if (character is Button) {
                        val characterButton: Button = character
                        characterButton.text = viewModel.gameBoard[i][j]
                    }
                }
            }
        }
    }

    /**
     * Displays the words in the hint suggestion
     */
    private fun initializeWordHints(listSize: Int = 3) {
        var i = 0
        var currentIndex = 0

        // TODO: Handle case when amount of words in the word list is different than number of cells
        while (i < listSize) {
            val child = binding.wordHintTable.getChildAt(i)
            var j = 0
            if (child is TableRow) {
                val currentRow: TableRow = child
                while (j < listSize) {
                    val wordHint = currentRow.getChildAt(j)
                    if (wordHint is TextView) {
                        val word: TextView = wordHint
                        word.text = viewModel.words[currentIndex]
                    }
                    j++
                    currentIndex++
                }
            }
            i++
        }
    }

    companion object {
        fun newInstance() = GameFragment()
    }

}
