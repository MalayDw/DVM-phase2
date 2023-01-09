package com.example.calculator

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.CalendarEntity
import android.view.Menu
import android.view.MenuItem
import android.widget.CalendarView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import androidx.core.content.ContextCompat
import com.example.calculator.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat


var count = 0
var openBracketCount = 0

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)
        binding = ActivityMainBinding.inflate(layoutInflater)

        clearBTN.setOnClickListener {
            input.text = ""
            output.text = ""
            count = 0
            openBracketCount = 0
        }

        openBracketBTN.setOnClickListener {
            input.text = addToInputText("(")
        }
        closedBracketBTN.setOnClickListener {
            input.text = addToInputText(")")
        }
        zeroBTN.setOnClickListener {
            input.text = addToInputText("0")
        }
        oneBTN.setOnClickListener {
            input.text = addToInputText("1")
        }
        twoBTN.setOnClickListener {
            input.text = addToInputText("2")
        }
        threeBTN.setOnClickListener {
            input.text = addToInputText("3")
        }
        fourBTN.setOnClickListener {
            input.text = addToInputText("4")
        }
        fiveBTN.setOnClickListener {
            input.text = addToInputText("5")
        }
        sixBTN.setOnClickListener {
            input.text = addToInputText("6")
        }
        sevenBTN.setOnClickListener {
            input.text = addToInputText("7")
        }
        eightBTN.setOnClickListener {
            input.text = addToInputText("8")
        }
        nineBTN.setOnClickListener {
            input.text = addToInputText("9")
        }
        decimalBTN.setOnClickListener {
            input.text = addToInputText(".")
        }
        divisionBTN.setOnClickListener {
            input.text = addToInputText("÷") // ALT + 0247
        }
        multiplicationBTN.setOnClickListener {
            input.text = addToInputText("×") // ALT + 0215
        }
        substractionBTN.setOnClickListener {
            input.text = addToInputText("-")
        }
        additionBTN.setOnClickListener {
            input.text = addToInputText("+")
        }
        powerBTN.setOnClickListener {
            input.text = addToInputText(",")
        }


        openSquareBTN.setOnClickListener {
            openBracketCount += 1
            if(openBracketCount == 1) {
                input.text = addToInputText("[")
            }
            else
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
            closeSquareBTN.setOnClickListener {
                count += 1
                if(count == 1) {
                    input.text = addToInputText("]×[")
                }
                else if(count == 2)
                {
                    run {
                        input.text = addToInputText("]")
                        var arr = input.text.toString()
                        val arrays = arr.split("×")
                        var arr1String = arrays[0]
                        arr1String = arr1String.removeRange(0,1)
                        arr1String = arr1String.dropLast(1)
                        val arr1 = arr1String.split(",").toTypedArray()
                        var arr2String = arrays[1]
                        arr2String = arr2String.removeRange(0,1)
                        arr2String = arr2String.dropLast(1)
                        val arr2 = arr2String.split(",").toTypedArray()
                        if(arrayMultiply(arr1,arr2) == null)
                        {
                                    output.text = "Error"
                                    val expression = input.text.toString()
                                    val result = output.text.toString()
                                    dbHelper.addCalculation(expression, result)
                                    dbHelper.deleteOldCalculations()
                        }
                        else{
                                output.text = arrayMultiply(arr1,arr2)
                                val expression = input.text.toString()
                                val result = output.text.toString()
                                dbHelper.addCalculation(expression, result)
                                dbHelper.deleteOldCalculations()
                        }
                    }
                }
                else
                {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }


        backSpaceBTN.setOnClickListener {
            val string = input.text.toString()
            if (string.isNotEmpty()) {
                input.text = string.substring(0, string.length - 1)
            } else {
                input.text = ""
            }
        }


        equalstoBTN.setOnClickListener {
            if(count == 0) {
                showResult()
            }
            val expression = input.text.toString()
            val result = output.text.toString()
            dbHelper.addCalculation(expression, result)
            dbHelper.deleteOldCalculations()
        }

        historyBTN.setOnClickListener {
            startActivity(Intent(this, DisplayDataActivity::class.java))
        }
}



    private fun arrayMultiply(array1: Array<String>,array2: Array<String>): String? {
        if(array1.size != array2.size) {
            return null
        }
        var result = Array(array1.size) { 0 }
            for (i in array1.indices) {
                result[i] = array1[i].toInt() * array2[i].toInt()
            }
            val stringResult: Array<String?> = arrayOfNulls(result.size)
            for (i in result.indices) {
                stringResult[i] = result[i].toString()
            }
            return stringResult.contentToString()
    }

    private fun getInputExpression(): String {
        var expression = input.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        return expression
    }

    private fun showResult() {
        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                output.text = "Error"
                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            } else {
                output.text = DecimalFormat("0.######").format(result).toString()
                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
        } catch (e: Exception) {
            output.text = "Error"
            output.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    private fun addToInputText(buttonValue: String): String {
        if(input.text.length > 19){
            Toast.makeText(this, "Can't enter more than 20 characters", Toast.LENGTH_SHORT).show()
            return "${input.text}"
        }
        return "${input.text}$buttonValue"
    }


}


