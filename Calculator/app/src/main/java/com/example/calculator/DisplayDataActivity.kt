package com.example.calculator

import android.annotation.SuppressLint
import com.example.calculator.adapter.Calculation
import com.example.calculator.adapter.HistoryAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_display_data.*

class DisplayDataActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: HistoryAdapter
    private val calculations = mutableListOf<Calculation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_data)

        dbHelper = DatabaseHelper(this)
        adapter = HistoryAdapter(calculations)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        loadCalculations()
    }

    private fun loadCalculations() {
        val cursor = dbHelper.getCalculations()
        if (cursor.count == 0) {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
        } else {
            while (cursor.moveToNext()) {
                val input = cursor.getString(cursor.getColumnIndexOrThrow("input"))
                val output = cursor.getString(cursor.getColumnIndexOrThrow("output"))
                calculations.add(Calculation(input, output))
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.clear_history){
        dbHelper.clearCalculation()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}