package com.infiren.crispyapp.model

data class ColumnInfo(
    val id: Int,
    val name: String,
    val cards: List<CardInfo>
)