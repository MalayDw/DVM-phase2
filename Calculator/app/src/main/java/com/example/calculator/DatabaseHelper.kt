package com.example.calculator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context): SQLiteOpenHelper(context,
    "Calculator.db",
    null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE calculations(id INTEGER PRIMARY KEY AUTOINCREMENT,input TEXT, output TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS calculations")
        onCreate(db)
    }

     fun addCalculation(input: String, output: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("input", input)
            put("output", output)
        }
        db.insert("calculations", null, values)
    }

    fun deleteOldCalculations(){
        val db = writableDatabase
        val numToKeep = 50
        if(getCalculations().count > numToKeep) {
            val cursor = db.query("calculations", null, null, null, null, null, null)

            if (cursor.moveToFirst()) {
                val rowId = cursor.getString(cursor.getColumnIndexOrThrow("input"))
                db.delete("calculations", "input=?", arrayOf(rowId))
            }
        }
    }

    fun clearCalculation(){
        val db = writableDatabase
        db.execSQL("DELETE FROM calculations")
    }


    fun getCalculations(): Cursor {
        val db = readableDatabase
        return db.rawQuery("Select * from calculations",null)
    }
}

