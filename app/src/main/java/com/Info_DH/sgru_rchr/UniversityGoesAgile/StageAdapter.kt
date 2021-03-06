package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StageAdapter(val mCtx: Context, val layoutResId: Int, val stageList: List<Stage>)

    : ArrayAdapter<Stage>(mCtx, layoutResId, stageList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val textViewName = view.findViewById<TextView>(R.id.TextViewName)
        val showStart1 = view.findViewById<TextView>(R.id.start1)
        val showEnd1 = view.findViewById<TextView>(R.id.end1)

        val stage = stageList[position]

        textViewName.text = stage.stageName
        showStart1.text = stage.phasenBeginn
        showEnd1.text = stage.phasenEnde


        return view
    }
}