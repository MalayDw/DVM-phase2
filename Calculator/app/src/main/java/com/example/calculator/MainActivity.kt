package com.example.calculator

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    private val CHANNEL_ID = "channelID"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0

    private var count = 0
    private var openBracketCount = 0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        dbHelper = DatabaseHelper(this)

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
            input.text = addToInputText("÷")
        }
        multiplicationBTN.setOnClickListener {
            input.text = addToInputText("×")
        }
        substractionBTN.setOnClickListener {
            input.text = addToInputText("-")
        }
        additionBTN.setOnClickListener {
            input.text = addToInputText("+")
        }
        commaBTN.setOnClickListener {
            input.text = addToInputText(",")
        }
        timerBTN.setOnClickListener{
            val expression = getInputExpression()
            val seconds = Expression(expression).calculate()
            if(seconds.isNaN()){
                output.text = "Error"
                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            else if(seconds < 0.0)
            {
                output.text = "Enter a Positive number"
                output.setTextSize(TypedValue.COMPLEX_UNIT_SP,35F)
                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
            else {
                val handler = Handler()
                val intent = Intent(this, MainActivity::class.java)
                val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(intent)
                    getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                val runnable = Runnable {
                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Time's UP!")
                        .setContentText("$seconds seconds are up")
                        .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build()

                    val notificationManager = NotificationManagerCompat.from(this)
                    notificationManager.notify(NOTIFICATION_ID, notification)


                    val timerInput = input.text.toString()
                    val totalTime = "$timerInput Seconds"
                    val time = "Timer"

                    dbHelper.addCalculation(time, totalTime)
                    dbHelper.deleteOldCalculations()
                }
                handler.postDelayed(runnable, (seconds * 1000).toLong())
            }

        }
        powerBTN.setOnClickListener{
            input.text = addToInputText("^")
        }
        sinBTN.setOnClickListener{
            input.text = addToInputText("sin(")
        }
        cosBTN.setOnClickListener{
            input.text = addToInputText("cos(")
        }
        tanBTN.setOnClickListener{
            input.text = addToInputText("tan(")
        }
        logBTN.setOnClickListener{
            input.text = addToInputText("log(")
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
                        val arr = input.text.toString()
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
                output.text = ""
            } else {
                input.text = ""
                output.text = ""
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
                val intent = Intent(this, DisplayDataActivity::class.java)
                val option =  ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                startActivity(intent, option)
        }
}



    private fun arrayMultiply(array1: Array<String>,array2: Array<String>): String? {
        if(array1.size != array2.size) {
            return null
        }
        val result = Array(array1.size) { 0 }
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
            }
            else {
                if(result == 69.000000) {
                    output.text = "NICE"
                    output.setTextColor(ContextCompat.getColor(this, R.color.red))
                }
                else {
                    output.text = DecimalFormat("0.######").format(result).toString()
                    output.setTextColor(ContextCompat.getColor(this, R.color.red))
                }
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT).apply{
            lightColor = Color.BLACK
            enableLights(true)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

}







