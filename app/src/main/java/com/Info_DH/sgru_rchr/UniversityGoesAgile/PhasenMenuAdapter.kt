package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.phasen_list_item.view.*



class PhasenMenuAdapter(val items : ArrayList<String>, val context: Context, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {



    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {





        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.phasen_list_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvStage?.text = items.get(position)
        (holder as ViewHolder).bind(position, clickListener)

    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvStage = view.tv_phase



    fun bind(part: Int, clickListener: (Int) -> Unit){

        tvStage.setOnClickListener { clickListener(part)}

    }
}