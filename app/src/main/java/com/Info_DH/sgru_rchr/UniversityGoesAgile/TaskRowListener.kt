package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.provider.ContactsContract

interface TaskRowListener {
    fun onTaskChange(objectId: String, isDone: Boolean)
    fun onTaskDelete(objectId: String)
    fun onTaskEdit(objectId: String, taskDesc:String)
    fun onTaskAssign(objectId: String)
}