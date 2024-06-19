package com.infiren.crispyapp.model

data class WorkspaceIdInfo(
    val id: Int,
    val name: String,
    val users: List<UserInfo>,
    val boards: List<BoardInfo>
)

data class BoardInfo(
    val id: Int,
    val name: String,
    val color: String
)
