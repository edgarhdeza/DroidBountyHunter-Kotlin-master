package edu.training.droidbountyhunterkotlin

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.training.droidbountyhunterkotlin.data.DatabaseBountyHunter
import edu.training.droidbountyhunterkotlin.models.Fugitivo

class AgregarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar)
    }

    fun guardarFugitivoPresionado(view: View){
        val nombreFugitivoTextView = findViewById<EditText>(R.id.nombreFugitivoTextView)

        val nombre = nombreFugitivoTextView.text.toString()

        if(nombre.isNotEmpty()){
            val database = DatabaseBountyHunter(this)
            database.insertarFugitivo(Fugitivo(0, nombre, 0))
            setResult(0)
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage("Nombre del fugitivo")
                .show()
        }
    }
}