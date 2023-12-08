package edu.training.droidbountyhunterkotlin.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import edu.training.droidbountyhunterkotlin.models.Fugitivo

const val DATABASE_NAME = "DroidBountyHunterDatabase"

const val VERSION = 3

const val TABLE_NAME_FUGITIVOS = "fugitivos"

const val COLUMN_NAME_ID = "id"
const val COLUMN_NAME_NAME = "name"
const val COLUMN_NAME_STATUS = "status"
const val COLUMN_NAME_PHOTO = "photo"
const val COLUMN_NAME_LATITUDE = "latitude"
const val COLUMN_NAME_LONGITUDE = "longitude"

class DatabaseBountyHunter(val context: Context) {
    private val TAG: String = DatabaseBountyHunter::class.java.simpleName

    private val TFugitivos = "CREATE TABLE " + TABLE_NAME_FUGITIVOS + " (" +
            COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, " +
            COLUMN_NAME_NAME + " TEXT NOT NULL, " +
            COLUMN_NAME_STATUS + " INTEGER, " +
            COLUMN_NAME_PHOTO + " TEXT, " +
            COLUMN_NAME_LATITUDE + " TEXT, " +
            COLUMN_NAME_LONGITUDE + " TEXT, " +
            "UNIQUE (" + COLUMN_NAME_NAME + ") ON CONFLICT REPLACE);"

    private  var helper: DBHelper? = null
    private  var database: SQLiteDatabase? = null


    fun open() : DatabaseBountyHunter{
        helper = DBHelper(context)
        database = helper!!.writableDatabase
        return this
    }

    fun close(){
        helper!!.close()
        database!!.close()
    }

    fun querySQL(sql: String, selectionArgs: Array<String>): Cursor{
        open()
        return database!!.rawQuery(sql, selectionArgs)
    }

    fun borrarFugitivo(fugitivo: Fugitivo){
        open()
        database!!.delete(TABLE_NAME_FUGITIVOS, COLUMN_NAME_ID + "=?", arrayOf(fugitivo.id.toString()))
        close()
    }

    fun actualizarFugitivo(fugitivo: Fugitivo){
        open()
        val values = ContentValues()
        values.put(COLUMN_NAME_NAME, fugitivo.name)
        values.put(COLUMN_NAME_STATUS, fugitivo.status)
        values.put(COLUMN_NAME_PHOTO, fugitivo.photo)
        values.put(COLUMN_NAME_LATITUDE, fugitivo.latitude)
        values.put(COLUMN_NAME_LONGITUDE, fugitivo.longitude)
        database!!.update(TABLE_NAME_FUGITIVOS, values, COLUMN_NAME_ID + "=?", arrayOf(fugitivo.id.toString()))
        close()
    }

    fun insertarFugitivo(fugitivo: Fugitivo){
        val values = ContentValues()
        values.put(COLUMN_NAME_NAME, fugitivo.name)
        values.put(COLUMN_NAME_STATUS, fugitivo.status)
        values.put(COLUMN_NAME_PHOTO, fugitivo.photo)
        values.put(COLUMN_NAME_LATITUDE, fugitivo.latitude)
        values.put(COLUMN_NAME_LONGITUDE, fugitivo.longitude)
        open()
        database!!.insert(TABLE_NAME_FUGITIVOS, null, values)
        close()
    }


    @SuppressLint("Range")
    fun obtenerFugitivos(status: Int) : Array<Fugitivo>{
        var fugitivos: Array<Fugitivo> = arrayOf()
        var dataCursor = querySQL("SELECT * FROM "+ TABLE_NAME_FUGITIVOS + " WHERE " +
                COLUMN_NAME_STATUS + "= ? ORDER BY " + COLUMN_NAME_NAME, arrayOf(status.toString()))
        if (dataCursor.count > 0)
        {
            fugitivos= generateSequence {
                if(dataCursor.moveToNext()) dataCursor else null
            }.map { myCursor ->
                val name = myCursor.getString(myCursor.getColumnIndex(COLUMN_NAME_NAME))
                val statusFugitivo = myCursor.getInt(myCursor.getColumnIndex(COLUMN_NAME_STATUS))
                val id = myCursor.getInt(myCursor.getColumnIndex(COLUMN_NAME_ID))
                val photo = myCursor.getString(myCursor.getColumnIndex(COLUMN_NAME_PHOTO))
                val latitude = myCursor.getDouble(myCursor.getColumnIndex(COLUMN_NAME_LATITUDE))
                val longitude = myCursor.getDouble(myCursor.getColumnIndex(COLUMN_NAME_LONGITUDE))
                return@map Fugitivo(id, name, statusFugitivo, photo, latitude, longitude)
            }.toList().toTypedArray()
        }

        return fugitivos
    }



    inner class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(TFugitivos)
            Log.d(TAG, "Creacion de la base de datos")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FUGITIVOS)
            onCreate(db)
        }
    }
}