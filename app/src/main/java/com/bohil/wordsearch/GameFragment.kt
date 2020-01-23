package com.bohil.wordsearch

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bohil.wordsearch.databinding.FragmentGameBinding


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: FragmentGameBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        //Toast.makeText(activity, "character is: $BOARD_LENGTH", Toast.LENGTH_LONG).show()
        // Game Board Initialization
        loadWords()
        fillTable()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this

        refreshTable()
        initializeWordHints()

        return binding.root
    }

    /**
     * Loads the words into the game board
     * Words in the word list are gone through in order
     */
    private fun loadWords() {
        viewModel.loadWords()
    }

    private fun fillTable() {
        viewModel.fillTable()
    }

    /**
     * Refreshes game board to display the updated character table
     */

    // TODO Implement case when its a new table. Currently colors do not change properly if theres a n
    // new game
    private fun refreshTable(boardSize: Int = 10) {
        // Setting the text for each button
        for (i in 0 until boardSize) {
            val child = binding.gameGrid.getChildAt(i)
            if (child is TableRow) {
                val currentRow: TableRow = child
                for (j in 0 until boardSize) {
                    val character = currentRow.getChildAt(j)
                    if (character is Button) {
                        val charBtn: Button = character
                        charBtn.text = viewModel.gameBoard[i][j]
                        // Adding the on clicks for each button
                        charBtnClick(charBtn)

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

        // TODO: Handle case when amount of words in word list is different than amount in text views
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

    /**
     * On Click logic for each character button
     */

    // TODO Fix how colors change when instance of new game
    private fun charBtnClick(btn: Button) {
        btn.setOnClickListener {
            // Changing button color
            val newColor = Color.argb(100, 145, 250, 145)
            btn.setBackgroundColor(
                if ((btn.background as ColorDrawable).color == Color.WHITE)
                    newColor else Color.WHITE
            )

            // adding the text to the user's current selection
            viewModel.addToSelection(btn.text.toString())
        }

    }

    companion object {
        fun newInstance() = GameFragment()
    }

}
