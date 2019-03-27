package com.Info_DH.sgru_rchr.UniversityGoesAgile

class Project{
    companion object Factory {
        fun create(): Project = Project()
        }

    var objectId: String? = null
    var projectName: String? = null
    var deadLine: String? = null

}