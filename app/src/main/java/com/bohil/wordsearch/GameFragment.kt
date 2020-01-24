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
import android.widget.Toast
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
        setTags(viewModel.BOARD_SIZE)
        initializeWordHints()

        binding.submitButton.setOnClickListener { submitWord() }

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
    private fun refreshTable(boardSize: Int = 10) {
        // TODO Implement case when its a new table
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
     * Changes the character button colors and adds character to current selection
     */
    private fun charBtnClick(btn: Button) {
        btn.setOnClickListener {
            if ((btn.background as ColorDrawable).color == Color.WHITE) {

                //If the character adds successfully, change color of button
                if (viewModel.addToSelection(btn)) {
                    // Changing button color
                    val newColor = Color.argb(100, 145, 250, 145)
                    btn.setBackgroundColor(
                        if ((btn.background as ColorDrawable).color == Color.WHITE)
                            newColor else Color.WHITE
                    )
                } else {
                    if (viewModel.currentText.toString().length == viewModel.BOARD_SIZE) {
                        Toast.makeText(activity, "MAXIMUM CHARACTERS REACHED", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                viewModel.removeFromSelection()
                btn.setBackgroundColor(Color.WHITE)
            }
        }
    }

    /**
     * Sets the tags for each button in the game board to help identify its location
     */
    private fun setTags(boardSize: Int = 10) {
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

    /**
     * Submits the word for validation
     * Updates the hint text view
     */
    private fun submitWord() {
        val pairs = viewModel.getSelectedPairs()

        // Determining whether the word was valid
        if (viewModel.submitWord()) {
            // Changing the color of those characters and making them unclickable
            for (pair in pairs) {
                val charBtn = getCharBtn(pair.first, pair.second)
                if (charBtn != null) {
                    charBtn.isClickable = false
                    charBtn.setBackgroundColor(Color.RED)
                }
            }

            // Updating the hint text views
            val hintText: TextView? = getHintTextView(viewModel.currentText.value.toString())
            if (hintText != null) hintText.alpha = 0.5.toFloat()
            //binding.invalidateAll()
        }
        // If selection invalid, reset colors of previous char btns
        else {
            for (pair in pairs) {
                val charBtn = getCharBtn(pair.first, pair.second)
                if (charBtn != null) {
                    charBtn.setBackgroundColor(Color.WHITE)
                }
            }
            binding.invalidateAll()
        }

        viewModel.clearSelection()
        //Checking game completion
        if (viewModel.wordsFound == viewModel.words.size) completeGame()
    }

    /**
     * Notifies user they have finished game
     */
    private fun completeGame() {
        Toast.makeText(activity, "Congratulations, You've won", Toast.LENGTH_LONG).show()
    }

    /**
     * Retrieves the Button that matches the index
     */
    private fun getCharBtn(x: Int, y: Int): Button? {
        val child = binding.gameGrid.getChildAt(x)
        if (child is TableRow) {
            val currentRow: TableRow = child
            val character = currentRow.getChildAt(y)
            if (character is Button) {
                val charBtn: Button = character
                return charBtn
            }
        }
        return null
    }

    /**
     * Retrieves the TextView of the hint that matches with the given word
     */
    private fun getHintTextView(text: String): TextView? {
        var i = 0
        var currentIndex = 0

        // TODO: Handle case when amount of words in word list is different than amount in text views
        while (i < viewModel.BOARD_SIZE) {
            val child = binding.wordHintTable.getChildAt(i)
            var j = 0
            if (child is TableRow) {
                val currentRow: TableRow = child
                while (j < viewModel.BOARD_SIZE) {
                    val wordHint = currentRow.getChildAt(j)
                    if (wordHint is TextView) {
                        val word: TextView = wordHint
                        if (word.text.toString() == text) {
                            return word
                        }
                    }
                    j++
                    currentIndex++
                }
            }
            i++
        }
        return null
    }


    companion object {
        fun newInstance() = GameFragment()
    }

}
