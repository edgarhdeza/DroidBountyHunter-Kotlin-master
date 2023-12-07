package edu.training.droidbountyhunterkotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.training.droidbountyhunterkotlin.data.DatabaseBountyHunter
import edu.training.droidbountyhunterkotlin.models.Fugitivo

class DetalleActivity : AppCompatActivity() {

    var fugitivo: Fugitivo? = null
    var database: DatabaseBountyHunter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle)

        fugitivo = intent.extras?.get("fugitivo") as Fugitivo

        //title = fugitivo!!.name //intent.extras?.get("titulo") as CharSequence?

        title = fugitivo!!.name + " - " + fugitivo!!.id

        val etiquetaMensaje = findViewById<TextView>(R.id.etiquetaMensaje)

        val botonCapturar = findViewById<Button>(R.id.botonCapturar)

        if(fugitivo!!.status == 0)  //if(intent.extras?.get("modo") == 0)
        {
            etiquetaMensaje.text = "El fugitivo sigue sueto..."
        }
        else
        {
            etiquetaMensaje.text = "Atrapado!!!"
            botonCapturar.visibility = View.GONE
        }
    }

    fun capturarFugitivoPresionado(view: View){
        database = DatabaseBountyHunter(this)
        fugitivo!!.status = 1
        database!!.actualizarFugitivo(fugitivo!!)
        setResult(0)
        finish()
    }

    fun eliminarFugitivoPresionado(view: View){
        database = DatabaseBountyHunter(this)
        database!!.borrarFugitivo(fugitivo!!)
        setResult(0)
        finish()
    }
}