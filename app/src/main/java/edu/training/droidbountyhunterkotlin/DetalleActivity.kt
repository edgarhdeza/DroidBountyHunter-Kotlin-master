package edu.training.droidbountyhunterkotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle)
        title = intent.extras?.get("titulo") as CharSequence?
        val etiquetaMensaje = findViewById<TextView>(R.id.etiquetaMensaje)
        if(intent.extras?.get("modo") == 0)
        {
            etiquetaMensaje.text = "El fugitivo sigue sueto..."
        }
        else
        {
            etiquetaMensaje.text = "Atrapado!!!"
        }
    }
}