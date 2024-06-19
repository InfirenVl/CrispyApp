package com.infiren.crispyapp.service

import com.infiren.crispyapp.model.AuthResponse
import com.infiren.crispyapp.model.BoardInfoModel
import com.infiren.crispyapp.model.BoardName
import com.infiren.crispyapp.model.UserInfo
import com.infiren.crispyapp.model.UserModel
import com.infiren.crispyapp.model.UserSignIn
import com.infiren.crispyapp.model.WorkspaceIdInfo
import com.infiren.crispyapp.model.WorkspaceInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/auth/signup")
    fun signup(@Body user: UserModel): Call<Void>

    @POST("/auth/signin")
    fun signin(@Body userSignIn: UserSignIn): Call<String>

    @GET("/main/users")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserInfo>

    @GET("/main/users/{id}")
    fun getUserProfileId(@Path("id") id: Int, @Header("Authorization") token: String): Call<UserInfo>

    @PUT("/main/users")
    fun changeUserPassword(@Header("Authorization") token: String, @Body passwordChangeRequest: Map<String, String>): Call<Void>

    @PATCH("/main/users")
    fun changeUserName(@Header("Authorization") token: String, @Body nameChangeRequest: Map<String, String>): Call<Void>

    @DELETE("/main/users")
    fun deleteUserAccount(@Header("Authorization") token: String): Call<Void>

    @GET("/main/boards/{boardId}")
    fun getBoard(@Header("Authorization") token: String, @Path("boardId") boardId: Int): Call<BoardInfoModel>

    @POST("/main/workspaces")
    fun createWorkspace(@Body name: String, @Header("Authorization") token: String): Call<Void>

    @PATCH("/main/workspaces/{workspaceId}")
    fun updateWorkspace(@Path("workspaceId") id: Int, @Body workspace: WorkspaceInfo, @Header("Authorization") token: String): Call<Void>

    @DELETE("/main/workspaces/{workspaceId}")
    fun deleteWorkspace(@Path("workspaceId") workspaceId: Int, @Header("Authorization") token: String): Call<Void>

    @GET("/main/workspaces/{id}")
    fun getWorkspace(@Path("id") id: Int, @Header("Authorization") token: String): Call<WorkspaceIdInfo>

    @POST("/main/workspaces/{workspaceId}/boards")
    fun createBoard(
        @Path("workspaceId") workspaceId: Int,
        @Body boardName: BoardName,
        @Header("Authorization") token: String
    ): Call<Void>

    @POST("/main/columns/{columnId}/cards")
    fun createCard(@Path("columnId") columnId: Int, @Body cardData: Map<String, String>, @Header("Authorization") token: String): Call<Void>

    @PUT("/main/workspaces/{workspaceId}/users/{username}")
    fun addUserToWorkspace(
        @Path("workspaceId") workspaceId: Int,
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Call<Void>
}
