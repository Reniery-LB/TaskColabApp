package com.taskcolab.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase principal de la aplicación.
 * @HiltAndroidApp inicializa Hilt para la inyección de dependencias en toda la app.
 */
@HiltAndroidApp
class TaskColabApp : Application()
