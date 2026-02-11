package com.example.tarea_12_conversor_monedas.Configuration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteConnection(context: Context?,name: String? = Transactions.dbname, factory: SQLiteDatabase.CursorFactory? = null, version: Int = Transactions.dbversion
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Transactions.CreateTableRates)
        db.execSQL(Transactions.CreateTableConversions)
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('HNL', 'USD', 0.040)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('CRC', 'USD', 0.0019)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(Transactions.DropTableRates)
        db.execSQL(Transactions.DropTableConversions)
        onCreate(db)
    }
    }
