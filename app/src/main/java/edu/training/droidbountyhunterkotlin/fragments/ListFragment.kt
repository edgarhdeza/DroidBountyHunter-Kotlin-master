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
import edu.training.droidbountyhunterkotlin.DetalleActivity
import edu.training.droidbountyhunterkotlin.R

const val SECTION_NUMBER : String = "section_number"

class ListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        return  inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val modo = requireArguments()[SECTION_NUMBER] as Int

        val dummyData = ArrayList<String>()

        dummyData.add("Sergio Anguiano")
        dummyData.add("Arturo Ceballos")
        dummyData.add("Jonatan Juarez")
        dummyData.add("Karen Muñoz")


        val adaptador = context?.let { ArrayAdapter<String>(it, R.layout.item_fugitivo_list, dummyData) }

        val fugitivosCapturadosList = view.findViewById<ListView>(R.id.listaFugitivosCapturados)

        fugitivosCapturadosList.adapter = adaptador
        fugitivosCapturadosList.setOnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(context, DetalleActivity::class.java)
            intent.putExtra("titulo", (view as TextView).text)
            intent.putExtra("modo", modo)

            startActivity(intent)
        }
    }
}