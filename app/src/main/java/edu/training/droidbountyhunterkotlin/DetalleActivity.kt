package edu.training.droidbountyhunterkotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
//import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import edu.training.droidbountyhunterkotlin.data.DatabaseBountyHunter
import edu.training.droidbountyhunterkotlin.models.Fugitivo
import edu.training.droidbountyhunterkotlin.network.NetworkServices
import edu.training.droidbountyhunterkotlin.network.OnTaskListener
import edu.training.droidbountyhunterkotlin.utils.PictureTools
import edu.training.droidbountyhunterkotlin.utils.PictureTools.Companion.MEDIA_TYPE_IMAGE
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.Date

class DetalleActivity : AppCompatActivity() {

    private var UDID: String? = ""

    var fugitivo: Fugitivo? = null
    var database: DatabaseBountyHunter? = null

    private var direccionImagen: Uri? = null
    private var pictureFugitive: ImageView? = null

    private val REQUEST_CODE_GPS = 1234
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        @SuppressLint("HardwareIds")
        UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setupLocationObjects()

        fugitivo = intent.extras?.get("fugitivo") as Fugitivo

        title = fugitivo!!.name + " - " + fugitivo!!.id

        val etiquetaMensaje = findViewById<TextView>(edu.training.droidbountyhunterkotlin.R.id.etiquetaMensaje)
        val botonCapturar = findViewById<Button>(edu.training.droidbountyhunterkotlin.R.id.botonCapturar)

        pictureFugitive = findViewById(R.id.pictureFugitivo)

        if(fugitivo!!.status == 0)
        {
            etiquetaMensaje.text = "El fugitivo sigue sueto..."
            activarGPS()
        }
        else
        {
            etiquetaMensaje.text = "Atrapado!!!"
            botonCapturar.visibility = View.GONE

            val bitmap = fugitivo!!.photo?.let {
                PictureTools.decodeSampledBitmapFromUri(it, 200, 200)
            }

            pictureFugitive?.setImageBitmap(bitmap)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detalles, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
        apagarGPS()
    }

    override fun onDestroy() {
        pictureFugitive?.setImageBitmap(null)
        System.gc()
        super.onDestroy()
    }

    private  fun setupLocationObjects(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult?.lastLocation != null) {
                    val location = locationResult.lastLocation
                    fugitivo!!.latitude = location.latitude
                    fugitivo!!.longitude = location.longitude
                } else {
                    Log.d("LocationCallback", "Location missing in callback.")
                }
            }
        }
    }

    fun mensajeDeCerrado(mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.create()
        builder.setTitle("Alerta!!!")
            .setMessage(mensaje)
            .setOnDismissListener {
                setResult(fugitivo!!.status)
                finish()
            }.show()
    }

    private fun obtenFotoDeCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        direccionImagen = PictureTools.getOutputMediaFileUri(this, MEDIA_TYPE_IMAGE)

        Log.d("CURSO", direccionImagen.toString())

        intent.putExtra(MediaStore.EXTRA_OUTPUT, direccionImagen)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(StartActivityForResult()) {
        Log.i("CURSO", "${it.resultCode}")
        if(it.resultCode == Activity.RESULT_OK){
            Log.i("CURSO", "${it.resultCode}")
            Log.i("CURSO", "ENTRO")
            fugitivo!!.photo = PictureTools.currentPhotoPath
            val bitmap = PictureTools.decodeSampledBitmapFromUri(PictureTools.currentPhotoPath, 200, 200)
            pictureFugitive?.setImageBitmap(bitmap)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PictureTools.REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.d("RequestPermissions", "Camera - Granted")
                obtenFotoDeCamara()
            } else {
                Log.d("RequestPermissions", "Camera - Not Granted")
            }
        }else if(requestCode == REQUEST_CODE_GPS){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                activarGPS()
            }else
            {
                Log.d("RequestPermissions", "GPS - Not Granted")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun activarGPS(){
        if(isGPSActivated()){
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            Toast.makeText(this,"Activando GPS...", Toast.LENGTH_LONG).show()

            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                // Got last known location. In some rare situations this can be null.
                Log.d("Curso", "Last Known Location: $location")
                //location?.let {
                    fugitivo!!.latitude = location.latitude
                    fugitivo!!.longitude = location.longitude
                //}
            }
        }
    }

    private fun apagarGPS(){
        try {
            Toast.makeText(this, "Desactivando GPS...", Toast.LENGTH_LONG).show()
            val removeTask = fusedLocationClient?.removeLocationUpdates(locationCallback)
            removeTask?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LocationRequest", "Location Callback removed.")
                } else {
                    Log.d("LocationRequest", "Failed to remove Location Callback.")
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error desactivando GPS $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun isGPSActivated(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
// Should we show an explanation
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)){
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_GPS)
                    return false
                }else {
//No explanation needed, we can request the permissions.
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_GPS)
                    return false
                }
            }else {
                return true
            }
        }else {
            return true
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(fugitivo!!.status == 1){
            menu!!.findItem(R.id.menu_capturar).setVisible(false)
            menu!!.findItem(R.id.menu_camera).setVisible(false)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_borrar -> {
                eliminarFugitivoPresionado()
                true
            }
            R.id.menu_capturar -> {
                capturarFugitivoPresionado()
                true
            }
            R.id.menu_camera -> {
                onFotoClick()
                true
            }
            R.id.menu_mapa -> {
                onMapClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun eliminarFugitivoPresionado(){ //view: View
        database = DatabaseBountyHunter(this)
        database!!.borrarFugitivo(fugitivo!!)
        setResult(0)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun capturarFugitivoPresionado() { //view: View
        database = DatabaseBountyHunter(this)
        fugitivo!!.status = 1
        fugitivo!!.date = LocalDateTime.now().toString()

        if (fugitivo!!.photo.isNullOrEmpty()){
            Toast.makeText(this,
                "Es necesario tomar la foto antes de capturar al fugitivo",
                Toast.LENGTH_LONG).show()
            return
        }

        database!!.actualizarFugitivo(fugitivo!!)

        lifecycleScope.launch {
            NetworkServices.execute("Atrapar", object: OnTaskListener {
                override fun tareaCompletada(respuesta: String) {
                    val obj = JSONObject(respuesta)
                    val mensaje = obj.optString("mensaje","")
                    mensajeDeCerrado(mensaje)
                }
                override fun tareaConError(codigo: Int, mensaje: String, error: String) {
                    Toast.makeText(this@DetalleActivity, "Ocurrio un problema en la comunicación con el WebService!!!", Toast.LENGTH_LONG).show()
                }
            })
        }

        setResult(1)
        //finish()
    }

    fun onFotoClick(){ //view: View
        if(PictureTools.permissionReadMemmory(this)){
            obtenFotoDeCamara()
        }
    }

    fun onMapClick() { //view: View
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("fugitivo", fugitivo)
        startActivity(intent)
    }
}