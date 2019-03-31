package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


class TaskAdapter(context: Context, taskList: MutableList<Task>) : BaseAdapter() {

    private val _inflater: LayoutInflater = LayoutInflater.from(context)
    private var _taskList = taskList
    private var _rowListener: TaskRowListener = context as TaskRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val objectId: String = _taskList.get(position).objectId as String
        val done: Boolean = _taskList.get(position).done as Boolean
        val taskDesc: String = _taskList.get(position).taskDesc as String
        val assignee: String = _taskList.get(position).assignee as String

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

        listRowHolder.desc.text = taskDesc //hier weise ich im Layout dem Feld txttaskdesc den richtigen String zu
        listRowHolder.done.isChecked = done
        if(assignee!="leer"){
            listRowHolder.assign.text = assignee
            listRowHolder.assign.visibility = View.VISIBLE
            listRowHolder.assign1.visibility = View.GONE

        } else {
            listRowHolder.assign1.visibility = View.VISIBLE
            listRowHolder.assign.visibility = View.GONE
        }

        listRowHolder.done.setOnClickListener {
            _rowListener.onTaskChange(objectId, !done)
        }

        listRowHolder.edit.setOnClickListener {
            _rowListener.onTaskEdit(objectId, taskDesc)
            }

        listRowHolder.assign1.setOnClickListener {
            _rowListener.onTaskAssign (objectId)
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
        val assign: TextView = row!!.findViewById(R.id.nickNm) as TextView
        val assign1: ImageView = row!!.findViewById(R.id.btnassign) as ImageView
    }
}