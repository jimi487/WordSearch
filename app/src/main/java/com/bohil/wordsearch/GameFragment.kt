package com.bohil.wordsearch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bohil.wordsearch.databinding.FragmentGameBinding
import kotlin.random.Random


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        val BOARD_SIZE = binding.gameRow0.virtualChildCount
        viewModel.gameBoard = Array(BOARD_SIZE) { Array(BOARD_SIZE) { "-" } }


        //Toast.makeText(activity, "character is: $BOARD_LENGTH", Toast.LENGTH_LONG).show()
        // Game Board Initialization
        setTags(BOARD_SIZE)
        randomizeWords()
        loadWords(BOARD_SIZE)
        initializeTable(BOARD_SIZE)

    }

    /**
     * Sets the tags for each button in the game board
     */
    private fun setTags(boardSize: Int) {
        val tagString = "char_btn"
        // Going through each table row
        for (i in 0 until boardSize) {
            val child = binding.gameGrid.getChildAt(i)
            if (child is TableRow) {
                val currentRow: TableRow = child
                // Going through each table cell
                for (j in 0 until boardSize) {
                    currentRow.getChildAt(j).tag = tagString + i.toString() + j.toString()
                }
            }
        }
    }

    private fun randomizeWords() {
        viewModel.randomizeWords()
    }

    /**
     * Loads the words into the game board
     * Words in the word list are gone through in order
     */
    private fun loadWords(boardSize: Int = 10) {
        var rowStart: Int
        var colStart: Int
        var orientation: Int
        var spaceAvail: Boolean

        for (word in viewModel.words) {
            //Toast.makeText(activity, "Current word is $word", Toast.LENGTH_LONG).show()
            // Deciding the word orientation
            // Horizontal: 0    Vertical: 1
            orientation = Random.nextInt(2)
            // Finding a position on the table for the word
            when (orientation) {
                0 -> {
                    do {
                        spaceAvail = true
                        colStart = if (word.length == boardSize) 0 else Random.nextInt(boardSize)
                        rowStart = Random.nextInt(boardSize)

                        // If the size of the word exceeds array bounds, use board size instead
                        for (char in colStart until (if (colStart + word.length < boardSize) colStart + word.length else boardSize)) {
                            if (viewModel.gameBoard[rowStart][char] != "-") {
                                spaceAvail = false
                            }
                        }
                    } while (word.length + colStart > boardSize || !spaceAvail)

                    // Placing word into table
                    var x = 0
                    while (x < word.length) {
                        viewModel.gameBoard[rowStart][colStart] = word[x].toString()
                        x++
                        colStart++
                    }
                }

                1 -> {
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

                    // Placing word into table
                    var x = 0
                    while (x < word.length) {
                        viewModel.gameBoard[rowStart][colStart] = word[x].toString()
                        x++
                        rowStart++
                    }
                }
            }
        }
        binding.invalidateAll()
    }

    /**
     * Refreshes the game board to display the updated character table
     */
    private fun initializeTable(boardSize: Int) {
        // Rest of table filled with random characters
        val ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz"
        for (row in 0 until boardSize)
            for (col in 0 until boardSize) {
                if (viewModel.gameBoard[row][col] == "-")
                    viewModel.gameBoard[row][col] =
                        ALLOWED_CHARACTERS[Random.nextInt(ALLOWED_CHARACTERS.length)].toString()
            }

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
        binding.invalidateAll()
    }

    companion object {
        fun newInstance() = GameFragment()
    }

}
