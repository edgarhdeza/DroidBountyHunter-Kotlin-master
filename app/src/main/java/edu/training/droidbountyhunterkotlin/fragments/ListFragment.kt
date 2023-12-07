package edu.training.droidbountyhunterkotlin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.training.droidbountyhunterkotlin.DetalleActivity
import edu.training.droidbountyhunterkotlin.FugitivoViewModel
import edu.training.droidbountyhunterkotlin.R
import edu.training.droidbountyhunterkotlin.data.DatabaseBountyHunter
import edu.training.droidbountyhunterkotlin.models.Fugitivo

const val SECTION_NUMBER : String = "section_number"

class ListFragment : Fragment() {

    private  val viewModel: FugitivoViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return  inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val modo = requireArguments()[SECTION_NUMBER] as Int

//        val dummyData = ArrayList<String>()
//        dummyData.add("Sergio Anguiano")
//        dummyData.add("Arturo Ceballos")
//        dummyData.add("Jonatan Juarez")
//        dummyData.add("Karen Muñoz")
//
//        val adaptador = context?.let { ArrayAdapter<String>(it, R.layout.item_fugitivo_list, dummyData) }
        //fugitivosCapturadosList.adapter = adaptador

        val fugitivosCapturadosList = view.findViewById<ListView>(R.id.listaFugitivosCapturados)

        actualizarDatos(fugitivosCapturadosList, modo)

        fugitivosCapturadosList.setOnItemClickListener { adapterView, view, position, id ->
//            val intent = Intent(context, DetalleActivity::class.java)
//            intent.putExtra("titulo", (view as TextView).text)
//            intent.putExtra("modo", modo)
//
//            startActivity(intent)

            val fugitivoList = fugitivosCapturadosList.tag as Array<Fugitivo>
            viewModel.selectFugitivo(fugitivoList[position])

        }
    }

    private fun actualizarDatos(listView: ListView?, modo: Int){
        val database = DatabaseBountyHunter(requireContext())
        val fugitivos = database.obtenerFugitivos(modo)
        if(fugitivos.isNotEmpty()){
            val values = ArrayList<String?>()
            fugitivos.mapTo(values){ it.name }

            val adaptador = ArrayAdapter<String>(requireContext(), R.layout.item_fugitivo_list, values)
            listView!!.adapter = adaptador
            listView.tag = fugitivos
        }
    }
}