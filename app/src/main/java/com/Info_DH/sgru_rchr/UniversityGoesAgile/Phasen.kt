package com.Info_DH.sgru_rchr.UniversityGoesAgile



class Phasen(

    val stageName: String,
    val phasenBeginn: String,
    val phasenEnde: String,
    val stageId: String
) {

    constructor() : this("", "", "", "") {
    }

    override fun toString(): String {
        return stageName as String
    }
}



