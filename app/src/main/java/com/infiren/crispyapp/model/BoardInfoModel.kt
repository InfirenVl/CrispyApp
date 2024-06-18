package com.infiren.crispyapp.model

data class BoardInfoModel(
    val id: Int,
    val name: String,
    val workspaceId: Int,
    val columns: List<Column>
)

data class Column(
    val id: Int,
    val name: String,
    val cards: List<Card>
)

data class Card(
    val id: Int,
    val name: String
)