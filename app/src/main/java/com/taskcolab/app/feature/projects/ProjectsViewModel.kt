package com.taskcolab.app.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskcolab.app.data.taskcolab.TaskColabRepository
import com.taskcolab.app.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val projects: List<Project> = emptyList(),
    val archivedProjects: List<Project> = emptyList(),
    val activeProject: Project? = null
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: TaskColabRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            repository.activeProject.collect { project ->
                _uiState.update { it.copy(activeProject = project) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val projectsResult = repository.getProjects()
            val archivedResult = repository.getProjects(archived = true)

            if (projectsResult.isSuccess || archivedResult.isSuccess) {
                val previous = _uiState.value
                val error = projectsResult.exceptionOrNull()?.message
                    ?: archivedResult.exceptionOrNull()?.message
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error,
                        projects = projectsResult.getOrDefault(previous.projects),
                        archivedProjects = archivedResult.getOrDefault(previous.archivedProjects),
                        activeProject = repository.activeProject.value
                    )
                }
            } else {
                val throwable = projectsResult.exceptionOrNull() ?: archivedResult.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable?.message ?: "No se pudieron cargar proyectos"
                    )
                }
            }
        }
    }

    fun refreshActiveOnly() {
        viewModelScope.launch {
            repository.getProjects()
                .onSuccess { projects ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            projects = projects,
                            activeProject = repository.activeProject.value
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    fun selectProject(project: Project) {
        repository.selectProject(project)
    }

    fun createProject(name: String, description: String, color: String, dueDate: String?) {
        viewModelScope.launch {
            repository.createProject(name, description, color, dueDate)
                .onSuccess { refresh() }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }

    fun pauseProject(project: Project) {
        viewModelScope.launch {
            repository.pauseProject(project)
                .onSuccess { refresh() }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }

    fun archiveProject(project: Project) {
        viewModelScope.launch {
            repository.archiveProject(project)
                .onSuccess { refresh() }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }

    fun restoreProject(project: Project) {
        viewModelScope.launch {
            repository.restoreProject(project)
                .onSuccess { refresh() }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }

    fun deleteArchivedProject(project: Project) {
        viewModelScope.launch {
            repository.deleteArchivedProject(project)
                .onSuccess { refresh() }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }
}
