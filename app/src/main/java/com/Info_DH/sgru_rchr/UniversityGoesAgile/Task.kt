package com.Info_DH.sgru_rchr.UniversityGoesAgile

class Task {
    companion object Factory {
        fun create(): Task = Task()
    }

    var objectId: String? = null
    var taskDesc: String? = null
    var done: Boolean? = false
    var author: String? = null
    var edit: String? = null
    var assignee: String? = null
}

