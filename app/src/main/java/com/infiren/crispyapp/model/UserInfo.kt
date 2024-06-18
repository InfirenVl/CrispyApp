package com.infiren.crispyapp.model

data class UserInfo(
    val id: Int,
    val name: String,
    val workspaces: List<WorkspaceInfo>
)

