package com.taskcolab.app.data.remote

import com.taskcolab.app.data.remote.dto.AuthResponse
import com.taskcolab.app.data.remote.dto.BoardListResponse
import com.taskcolab.app.data.remote.dto.ConversationListResponse
import com.taskcolab.app.data.remote.dto.ConversationResponse
import com.taskcolab.app.data.remote.dto.CreateConversationRequest
import com.taskcolab.app.data.remote.dto.CreateProjectRequest
import com.taskcolab.app.data.remote.dto.CreateTaskRequest
import com.taskcolab.app.data.remote.dto.DeleteTasksRequest
import com.taskcolab.app.data.remote.dto.DeleteConversationRequest
import com.taskcolab.app.data.remote.dto.LoginRequest
import com.taskcolab.app.data.remote.dto.MessageListResponse
import com.taskcolab.app.data.remote.dto.ProjectListResponse
import com.taskcolab.app.data.remote.dto.ProjectResponse
import com.taskcolab.app.data.remote.dto.RegisterRequest
import com.taskcolab.app.data.remote.dto.ReportDashboardResponse
import com.taskcolab.app.data.remote.dto.SendMessageRequest
import com.taskcolab.app.data.remote.dto.SendMessageResponse
import com.taskcolab.app.data.remote.dto.SyncChangesResponse
import com.taskcolab.app.data.remote.dto.TaskListResponse
import com.taskcolab.app.data.remote.dto.TaskMutationResponse
import com.taskcolab.app.data.remote.dto.UpdateProjectRequest
import com.taskcolab.app.data.remote.dto.UpdateTaskRequest
import com.taskcolab.app.data.remote.dto.UserListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
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
        @Query("project_id") projectId: Int? = null,
        @Query("status") status: String? = null
    ): TaskListResponse

    @POST("tasks/index.php")
    suspend fun createTask(@Body request: CreateTaskRequest): TaskMutationResponse

    @PATCH("tasks/index.php")
    suspend fun updateTask(@Body request: UpdateTaskRequest): TaskMutationResponse

    @HTTP(method = "DELETE", path = "tasks/index.php", hasBody = true)
    suspend fun deleteTasks(@Body request: DeleteTasksRequest): TaskMutationResponse

    @GET("boards/index.php")
    suspend fun getBoards(@Query("project_id") projectId: Int? = null): BoardListResponse

    @GET("projects/index.php")
    suspend fun getProjects(): ProjectListResponse

    @POST("projects/index.php")
    suspend fun createProject(@Body request: CreateProjectRequest): ProjectResponse

    @PATCH("projects/index.php")
    suspend fun updateProject(@Body request: UpdateProjectRequest): ProjectResponse

    @HTTP(method = "DELETE", path = "projects/index.php", hasBody = true)
    suspend fun archiveProject(@Body request: UpdateProjectRequest): ProjectResponse

    @GET("chat/conversations.php")
    suspend fun getConversations(): ConversationListResponse

    @POST("chat/conversations.php")
    suspend fun createConversation(@Body request: CreateConversationRequest): ConversationResponse

    @HTTP(method = "DELETE", path = "chat/conversations.php", hasBody = true)
    suspend fun deleteConversation(@Body request: DeleteConversationRequest): ConversationResponse

    @GET("chat/messages.php")
    suspend fun getMessages(
        @Query("conversation_id") conversationId: Int,
        @Query("after_id") afterId: Int = 0
    ): MessageListResponse

    @POST("chat/messages.php")
    suspend fun sendMessage(@Body request: SendMessageRequest): SendMessageResponse

    @GET("reports/dashboard.php")
    suspend fun getReportDashboard(): ReportDashboardResponse

    @GET("users/index.php")
    suspend fun getUsers(): UserListResponse

    @GET("sync/changes.php")
    suspend fun getSyncChanges(
        @Query("since_id") sinceId: Long = 0,
        @Query("limit") limit: Int = 50
    ): SyncChangesResponse
}
