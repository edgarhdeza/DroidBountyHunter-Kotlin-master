package edu.training.droidbountyhunterkotlin.network

import android.hardware.usb.UsbEndpoint
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class NetworkServices {
    companion object {
        private  val TAG = NetworkServices::class.java.simpleName

        private const val ENDPOINT_FUGITIVOS = "http://3.13.226.218/droidBHServices.svc/fugitivos"
        private const val ENDPOINT_ATRAPADOS = "http://3.13.226.218/droidBHServices.svc/atrapados"

        private var JSONStr: String = ""
        private var tipo: SERVICE_TYPE = SERVICE_TYPE.FUGITIVOS
        private var codigo: Int = 0
        private var mensaje: String = ""
        private var error: String = ""

        suspend fun execute(param: String?, listener: OnTaskListener, uuid: String? = null) {
            val result = withContext(Dispatchers.IO) {
                execute(param, uuid)
            }
            if (result) {
                listener.tareaCompletada(JSONStr)
            } else {
                listener.tareaConError(codigo, mensaje, error)
            }
        }

        private fun execute(param: String?, uuid: String? = null): Boolean{
            val esFugitivo = param.equals("Fugitivos", true)
            tipo = if (esFugitivo) SERVICE_TYPE.FUGITIVOS else SERVICE_TYPE.ATRAPADOS
            var urlConnection: HttpURLConnection? = null

            try{
                val url = if(esFugitivo) ENDPOINT_FUGITIVOS else ENDPOINT_ATRAPADOS
                urlConnection = getStructuredRequest(
                    tipo,
                    if (esFugitivo) ENDPOINT_FUGITIVOS else ENDPOINT_ATRAPADOS,
                    uuid ?: ""
                )
                Log.d("CURSO", "")

                val inputStream = urlConnection?.inputStream ?: return false

                val reader = BufferedReader(InputStreamReader(inputStream))
                val buffer = StringBuffer()
                do{
                    val line: String? = reader.readLine()
                    if(line != null) buffer.append(line).append("\n")
                } while (line != null)

                if(buffer.isEmpty()) return false

                JSONStr = buffer.toString()

                return true
            } catch (exception: FileNotFoundException){
                manageError(urlConnection)
                return false
            } catch (exception: IOException) {
                manageError(urlConnection)
                return false
            } catch (exception: Exception ) {
                manageError(urlConnection)
                return false
            } finally {
                urlConnection?.disconnect()
            }
        }




        private fun manageError(urlConnection: HttpURLConnection?) {
            if (urlConnection != null) {
                try {
                    codigo = urlConnection.responseCode
                    if (urlConnection.errorStream != null) {
                        val inputStream = urlConnection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val buffer = StringBuffer()
                        do {
                            val line: String? = reader.readLine()
                            if (line != null) buffer.append(line).append("\n")
                        } while (line != null)
                        error = buffer.toString()
                    } else {
                        mensaje = urlConnection.responseMessage

                    }
                    error = urlConnection.errorStream.toString()
                    Log.e(TAG, "Error: $error, code: $codigo")
                } catch (e1: IOException) {
                    e1.printStackTrace()
                    Log.e(TAG, "Error")
                }
            } else {
                codigo = 105
                mensaje = "Error: No internet connection"
                Log.e(TAG, "code: $codigo, $mensaje")
            }
        }





        private  fun getStructuredRequest(type: SERVICE_TYPE, endpoint: String, id: String) : HttpURLConnection {
            val TIME_OUT = 5000
            val urlConnection: HttpURLConnection
            val url = URL(endpoint)

            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = TIME_OUT
            urlConnection.setRequestProperty("Content-Type", "application/json")

            if(type == SERVICE_TYPE.FUGITIVOS){
                urlConnection.requestMethod = "GET"
                urlConnection.connect()
            } else {
                urlConnection.requestMethod = "POST"
                urlConnection.doInput = true
                urlConnection.doOutput = true
                urlConnection.connect()
                val objectJSON = JSONObject()

                objectJSON.put("UDIDString", id)
                val dataOutputStream = DataOutputStream(urlConnection.getOutputStream())
                dataOutputStream.write(objectJSON.toString().toByteArray())
                dataOutputStream.flush()
                dataOutputStream.close()
            }

            return urlConnection
        }

    }
}

enum class SERVICE_TYPE{
    FUGITIVOS, ATRAPADOS
}

interface OnTaskListener{
    fun tareaCompletada(respuesta: String)
    fun tareaConError(codigo: Int, mensaje: String, error: String)
}