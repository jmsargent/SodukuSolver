package com.example.sodukusolver

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    // lateinits
    //    private lateinit var solveButton: Button
    private lateinit var dialog: TextView
    private lateinit var solveButton: Button
    private lateinit var textRows: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sodukuBoard = SodukuBoard()

        // initiate text rows
        textRows = arrayOf(
            findViewById(R.id.editTextNumberSigned),
            findViewById(R.id.editTextNumberSigned1),
            findViewById(R.id.editTextNumberSigned2),
            findViewById(R.id.editTextNumberSigned3),
            findViewById(R.id.editTextNumberSigned4),
            findViewById(R.id.editTextNumberSigned5),
            findViewById(R.id.editTextNumberSigned6),
            findViewById(R.id.editTextNumberSigned7),
            findViewById(R.id.editTextNumberSigned8)
        )

        // attach listener for text rows
        for (i in 0..8) {
            textRows[i].setOnClickListener() {
                sodukuBoard.updoku(i, textRows[i].text.toString())
            }
        }

        solveButton = findViewById(R.id.button)
        dialog = findViewById(R.id.textView)

        // shows the calculated solution
        solveButton.setOnClickListener() { updateEditTextFields(sodukuBoard) }

    }

    private fun updateEditTextFields(sodukuBoard: SodukuBoard) {

        for (i in 0..8) {
            textRows[i].setText(sodukuBoard.getCellRows(i))
        }
    }
}

class SodukuBoard {
    // contains the digit for each cell
    var cells = arrayOf<Array<Int>>()
    var solvedUku = arrayOf<Array<Int>> ()
    // contains the amount of unique neighbours each cell has
    // a neighbour is defined by how many cells that currently contains a number provide information to a given cell
    var neightbours2 = arrayOf<Array<Array<Int>>>()
    var rows: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    // primary constructor
    init {
        cells = Array(9) { Array<Int>(9) { 0 } }
        solvedUku = Array(9) { Array<Int>(9) { 0 } }
        neightbours2 = Array(9) { Array(9) { Array<Int>(9) { 0 } } }
        initiateNeighbours(neightbours2)
    }


    fun updoku(row: Int, values: String) {
        for (i in 1..9) {
            cells[row][i] = (values[i].toInt())

        }
    }

    /*
    private fun updateNeighbours(row: Int, col: Int) {

        // add one to each cell thats vertically and horizontally aligned with the cell
        for (i in 1..9) {
            neightbours[row][i]++
            neightbours[i][col]++
        }

        if (row in 1..3) {
            if (col in 1..3) {

                // add +1 to every cell in the first quadrant
                increment2dRange(1..3, 1..3, neightbours)

            } else if (col in 4..6) {
                //2:nd qudrant
                // add one
                increment2dRange(1..3, 4..6, neightbours)

            } else if (col in 7..9) {
                // 3:rd quadrant
                increment2dRange(1..3, 7..9, neightbours)
            }
        } else if (row in 4..6) {
            if (col in 1..3) {

                //4th
                increment2dRange(4..6, 1..3, neightbours)

            } else if (col in 4..6) {
                //5th
                increment2dRange(4..6, 4..6, neightbours)

            } else if (col in 7..9) {
                //6th
                increment2dRange(4..6, 7..9, neightbours)
            }
        } else if (row in 7..9) {
            if (col in 1..3) {
                //7th
                increment2dRange(7..9, 1..3, neightbours)

            } else if (col in 4..6) {
                //8th
                increment2dRange(7..9, 4..6, neightbours)

            } else if (col in 7..9) {
                //9th
                increment2dRange(7..9, 7..9, neightbours)
            }
        }
    }

    private fun increment2dRange(rowRange: IntRange, colRange: IntRange, array: Array<Array<Int>>) {

        for (i in rowRange) {
            for (j in colRange) {
                array[i][j]++
            }
        }
    }

     */
    fun getCellRows(row: Int): Int {

        var sum = 0

        // weighs the different numbers differently in order for them to get the right order
        // converts power to int since theres to built in power function for ints

        for (i in 0..8) {
            sum += cells[row][8 - i] * 10.0.pow(i).toInt()
        }
        return sum
    }


    private fun updateNeightbors2(row: Int, col: Int, value: Int) {

        // Value = 0 means that its an unknown
        if (value != 0) {
            // If same row as the value, want to remove the value from the matrix
            for (c in 0..8) {
                neightbours2[row][c][(value - 1)] = 0
            }

            // if same col as the value, we want to remove the value
            for (r in 0..8) {
                neightbours2[r][col][(value - 1)] = 0
            }

            // if in the same submatrix as the value we want to remove the values
            if (row in 0..2) {
                if (col in 0..2) {
                    intervalRemove(0..2, 0..2, value)
                } else if (col in 3..5) {
                    intervalRemove(0..2, 3..5, value)
                } else if (col in 6..8) {
                    intervalRemove(0..2, 6..8, value)
                }
            } else if (row in 3..5) {
                if (col in 0..2) {
                    intervalRemove(3..5, 0..2, value)
                } else if (col in 3..5) {
                    intervalRemove(3..5, 3..5, value)
                } else if (col in 6..8) {
                    intervalRemove(3..5, 6..8, value)
                }
            } else if (row in 6..8) {
                if (col in 0..2) {
                    intervalRemove(6..8, 0..2, value)
                } else if (col in 3..5) {
                    intervalRemove(6..8, 3..5, value)
                } else if (col in 6..8) {
                    intervalRemove(6..8, 6..8, value)
                }
            }
        }
    }

    private fun initiateNeighbours(array: Array<Array<Array<Int>>>) {
        for (i in 0..8) {
            for (j in 0..8) {
                for (k in 0..8) {
                    array[i][j][k] = (k + 1)
                }
            }
        }
    }

    // removes available neighbours for a given interval
    private fun intervalRemove(rowInterval: IntRange, colInterval: IntRange, value: Int) {

        for (r in rowInterval) {
            for (c in colInterval) {
                neightbours2[r][c][value - 1] = 0
            }
        }
    }

    private fun solve(){

        var maxNrOfNeighbours: Int = 0;
        var remainingDigit: Int = 0

        for(row in 0..8){
            for(col in 0..8){
                for(value in 0..8){
                    if(neightbours2[row][col][value]!=0){
                        maxNrOfNeighbours ++
                        remainingDigit = value + 1
                        if (maxNrOfNeighbours > 1)
                            break;
                    }else if(value == 8 && neightbours2[row][col][value]==0)
                        cells[row][col] = remainingDigit
                }
                maxNrOfNeighbours = 0
                remainingDigit = 0
            }
        }
    }
}


