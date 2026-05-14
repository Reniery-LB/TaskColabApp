package com.taskcolab.app.data.remote

import com.taskcolab.app.data.remote.dto.AuthResponse
import com.taskcolab.app.data.remote.dto.BoardListResponse
import com.taskcolab.app.data.remote.dto.LoginRequest
import com.taskcolab.app.data.remote.dto.RegisterRequest
import com.taskcolab.app.data.remote.dto.SyncChangesResponse
import com.taskcolab.app.data.remote.dto.TaskListResponse
import com.taskcolab.app.data.remote.dto.UserListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TaskColabApi {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("auth/me.php")
    suspend fun me(): AuthResponse

    @POST("auth/logout.php")
    suspend fun logout()

    @GET("tasks/index.php")
    suspend fun getTasks(
        @Query("board_id") boardId: Int? = null,
        @Query("status") status: String? = null
    ): TaskListResponse

    @GET("boards/index.php")
    suspend fun getBoards(): BoardListResponse

    @GET("users/index.php")
    suspend fun getUsers(): UserListResponse

    @GET("sync/changes.php")
    suspend fun getSyncChanges(
        @Query("since_id") sinceId: Long = 0,
        @Query("limit") limit: Int = 50
    ): SyncChangesResponse
}
