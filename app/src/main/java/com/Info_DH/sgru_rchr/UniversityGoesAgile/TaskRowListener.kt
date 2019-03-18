package com.Info_DH.sgru_rchr.UniversityGoesAgile

interface TaskRowListener {
    fun onTaskChange(objectId: String, isDone: Boolean)
    fun onTaskDelete(objectId: String)
}