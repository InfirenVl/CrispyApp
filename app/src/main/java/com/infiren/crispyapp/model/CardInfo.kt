package com.infiren.crispyapp.model

data class CardInfo(
    val id: Int,
    val name: String,
    val description: String?,
    val creationTime: String,
    val deadLineTime: String,
    val storyPoints: Int
)