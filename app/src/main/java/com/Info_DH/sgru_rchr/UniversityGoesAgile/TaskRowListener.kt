package com.Info_DH.sgru_rchr.UniversityGoesAgile


//Like a constructor for methods, to edit the task of the right row
interface TaskRowListener {
    fun onTaskChange(objectId: String, isDone: Boolean)
    fun onTaskDelete(objectId: String)
    fun onTaskEdit(objectId: String, taskDesc:String)
    fun onTaskAssign(objectId: String)
    fun onTaskAssigndelete (objectId: String)
}