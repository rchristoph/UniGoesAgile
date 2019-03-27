package com.Info_DH.sgru_rchr.UniversityGoesAgile

//import android.content.Context
import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.content.Context
import android.support.v4.app.Fragment
import com.google.firebase.database.snapshot.EmptyNode


class TaskAdapter(val frag: Fragment, context: Context, taskList: MutableList<Task>) : BaseAdapter() {

    private val _inflater: LayoutInflater = LayoutInflater.from(context)
    private var _taskList = taskList
     var _rowListener: com.Info_DH.sgru_rchr.UniversityGoesAgile.TaskRowListener = frag as com.Info_DH.sgru_rchr.UniversityGoesAgile.TaskRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val objectId: String = _taskList.get(position).objectId as String
        val itemText: String = _taskList.get(position).taskDesc as String
        val done: Boolean = _taskList.get(position).done as Boolean
        val taskDesc: String = _taskList.get(position).taskDesc as String

        val view: View
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = _inflater.inflate(R.layout.task_rows, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.desc.text = itemText
        listRowHolder.done.isChecked = done


        listRowHolder.done.setOnClickListener {
            _rowListener.onTaskChange(objectId, !done)
        }

        listRowHolder.edit.setOnClickListener {
            _rowListener.onTaskEdit(objectId, taskDesc)
            }



        listRowHolder.remove.setOnClickListener {
            _rowListener.onTaskDelete(objectId) }

        return view
    }




        override fun getItem(index: Int): Any {
        return _taskList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return _taskList.size
    }

    //das sind die Funktionen hinter den Icons in der Liste
    private class ListRowHolder(row: View?) {
        val desc: TextView = row!!.findViewById(R.id.txtTaskDesc) as TextView
        val done: CheckBox = row!!.findViewById(R.id.chkDone) as CheckBox
        val remove: ImageButton = row!!.findViewById(R.id.btnRemove) as ImageButton
        val edit: ImageView = row!!.findViewById(R.id.editTask) as ImageView

    }

    interface TaskRowListener {

        fun onTaskChange(objectId: String, isDone: Boolean)
        fun onTaskDelete(objectId: String)
        fun onTaskEdit(objectId: String, taskDesc:String)

    }

}