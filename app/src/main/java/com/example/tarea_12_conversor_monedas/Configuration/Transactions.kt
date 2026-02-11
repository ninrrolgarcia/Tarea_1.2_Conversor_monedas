package com.example.tarea_12_conversor_monedas.Configuration

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.tarea_12_conversor_monedas.Models.Conversion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Transactions(context: Context) {

    private val dbHelper = SQLiteConnection(context)

    fun getRate(fromCode: String): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${Companion.rate} FROM ${Companion.tbRates} WHERE ${Companion.from_code} = ?",
            arrayOf(fromCode)
        )

        var exchangeRate = 0.0
        if (cursor.moveToFirst()) {
            exchangeRate = cursor.getDouble(0)
        }
        cursor.close()
        return exchangeRate
    }

    fun insertConversion(from: String, to: String, amount: Double, result: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(Companion.from_code, from)
            put(Companion.to_code, to)
            put(Companion.amount, amount)
            put(Companion.result, result)
            put(Companion.date, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
        }
        val id = db.insert(Companion.tbConversions, null, values)
        db.close()
        return id
    }

    fun getAllConversions(): List<Conversion> {
        val list = mutableListOf<Conversion>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(Companion.SelectAllConversions, null)

        if (cursor.moveToFirst()) {
            do {
                val item = Conversion(
                    id = cursor.getInt(0),
                    fromCode = cursor.getString(1),
                    toCode = cursor.getString(2),
                    amount = cursor.getDouble(3),
                    result = cursor.getDouble(4),
                    date = cursor.getString(5),
                    isFavorite = cursor.getInt(6)
                )
                list.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun toggleFavorite(id: Int, currentStatus: Int) {
        val db = dbHelper.writableDatabase
        val newStatus = if (currentStatus == 1) 0 else 1
        val values = ContentValues().apply {
            put(Companion.is_favorite, newStatus)
        }
        db.update(Companion.tbConversions, values, "${Companion.id} = ?", arrayOf(id.toString()))
        db.close()
    }

    companion object {
        const val dbname = "DBPM01"
        const val dbversion = 1

        const val tbRates = "rates"
        const val tbConversions = "conversions"

        const val id = "id"
        const val from_code = "from_code"
        const val to_code = "to_code"
        const val rate = "rate"
        const val amount = "amount"
        const val result = "result"
        const val date = "date"
        const val is_favorite = "is_favorite"

        const val CreateTableRates = """
            CREATE TABLE $tbRates (
                $id INTEGER PRIMARY KEY AUTOINCREMENT,
                $from_code TEXT,
                $to_code TEXT,
                $rate REAL
            )
        """

        const val CreateTableConversions = """
            CREATE TABLE $tbConversions (
                $id INTEGER PRIMARY KEY AUTOINCREMENT,
                $from_code TEXT,
                $to_code TEXT,
                $amount REAL,
                $result REAL,
                $date TEXT,
                $is_favorite INTEGER DEFAULT 0
            )
        """

        const val DropTableRates = "DROP TABLE IF EXISTS $tbRates"
        const val DropTableConversions = "DROP TABLE IF EXISTS $tbConversions"

        const val SelectAllConversions = "SELECT * FROM $tbConversions"
    }
}