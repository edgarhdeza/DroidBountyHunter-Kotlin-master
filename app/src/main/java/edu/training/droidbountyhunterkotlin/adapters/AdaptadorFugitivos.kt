package edu.training.droidbountyhunterkotlin.adapters

import android.content.Context
import android.icu.text.LocaleDisplayNames.UiListItem
import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import edu.training.droidbountyhunterkotlin.R
import edu.training.droidbountyhunterkotlin.models.Fugitivo
import edu.training.droidbountyhunterkotlin.utils.PictureTools
import java.security.AccessControlContext

class AdaptadorFugitivos(val context: Context, val listItem: ArrayList<Fugitivo>) : BaseAdapter() {
    override fun getCount(): Int {
        return listItem.size
    }

    override fun getItemId(position: Int): Long {
        return listItem[position].id.toLong()
    }

    override fun getItem(position: Int): Any {
        return  listItem[position]
    }

    override fun getView(position: Int, view: View?, group: ViewGroup): View {
        var myView = view

        val fugitivo = getItem(position) as Fugitivo

        if(myView == null)
        {
            val inflater: LayoutInflater = LayoutInflater.from(context)

            myView = inflater.inflate(R.layout.item_fugitivo_list, null)
        }

        val bitmap = fugitivo!!.photo?.let {
            PictureTools.decodeSampledBitmapFromUri(it, 200, 200)
        }

        val imagenViewFugitivo = myView!!.findViewById<ImageView>(R.id.imagenFugitivo)
        imagenViewFugitivo.setImageBitmap(bitmap)

        val nombreFugitivo = myView!!.findViewById<TextView>(R.id.textoRenglon)
        nombreFugitivo.text = fugitivo.name

        val fechaCaptura = myView!!.findViewById<TextView>(R.id.fechaCaptura)
        fechaCaptura.text = fugitivo.date

        return myView
    }
}