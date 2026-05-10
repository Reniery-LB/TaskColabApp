package com.taskcolab.app.feature.reports

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.taskcolab.app.R
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Color as AndroidColor

@Composable
fun ReportsPlaceholderScreen(
    onNavigateToBoards: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val stats = remember {
        ReportStats(
            totalTasks = 4,
            inProgress = 1,
            pending = 1,
            completed = 2,
            boards = 3,
            activeUsers = 2,
            progress = buildProgressDistribution(
                pending = 1,
                inProgress = 1,
                completed = 2
            )
        )
    }
    val exportPdf = {
        val result = runCatching { exportSystemStatsPdf(context, stats) }
        val message = result.fold(
            onSuccess = { "PDF guardado en ${it.location}" },
            onFailure = { "No se pudo exportar el PDF" }
        )
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            exportPdf()
        } else {
            Toast.makeText(context, "Permiso requerido para guardar en Descargas", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            ReportsHeader()
        },
        bottomBar = {
            TaskColabBottomBar(
                selectedRoute = ReportsNavItem.REPORTS,
                onBoards = onNavigateToBoards,
                onTasks = onNavigateToTasks,
                onReports = onNavigateToReports,
                onUsers = onNavigateToUsers,
                onProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 24.dp, top = 22.dp, end = 24.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ReportMetricCard(
                            label = "Total Tareas",
                            value = stats.totalTasks.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        ReportMetricCard(
                            label = "En proceso",
                            value = stats.inProgress.toString(),
                            accentColor = Color(0xFFE88A00),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ReportMetricCard(
                            label = "Pendiente",
                            value = stats.pending.toString(),
                            accentColor = Color(0xFFFF1010),
                            modifier = Modifier.weight(1f)
                        )
                        ReportMetricCard(
                            label = "Completado",
                            value = stats.completed.toString(),
                            accentColor = Color(0xFF00B636),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                ReportProgressPanel(stats.progress)
            }

            item {
                Button(
                    onClick = {
                        val needsLegacyPermission = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED

                        if (needsLegacyPermission) {
                            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            exportPdf()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Exportar PDF",
                        style = MaterialTheme.typography.titleLarge,
                        color = TaskColabWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabBlue)
            .padding(start = 18.dp, top = 46.dp, end = 22.dp, bottom = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reportes",
            style = MaterialTheme.typography.headlineLarge,
            color = TaskColabWhite,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(TaskColabWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RC",
                style = MaterialTheme.typography.headlineMedium,
                color = TaskColabInk,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReportMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color? = null
) {
    Surface(
        modifier = modifier
            .height(126.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.12f))
            .border(BorderStroke(1.dp, Color(0xFFCFCFCF)), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxSize()
                    .background(accentColor ?: TaskColabWhite)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ReportProgressPanel(progress: List<ReportProgress>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.14f))
            .border(BorderStroke(2.dp, TaskColabBlue), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Progreso por tablero",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            progress.forEach { item ->
                ReportProgressRow(item)
            }
        }
    }
}

@Composable
private fun ReportProgressRow(progress: ReportProgress) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = progress.label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B4B4B),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${progress.percent}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B4B4B),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .background(Color(0xFFD9D9D9), RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.percent / 100f)
                    .height(9.dp)
                    .background(progress.color, RoundedCornerShape(50))
            )
        }
    }
}

private enum class ReportsNavItem(
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    BOARDS("Tableros", R.drawable.icn_tableros_seleccionado, R.drawable.icn_tableros_seleccionado),
    TASKS("Tareas", R.drawable.icn_tareas_seleccionado, R.drawable.icn_tareas),
    REPORTS("Reportes", R.drawable.icn_reportes_seleccionado, R.drawable.icn_reportes),
    USERS("Usuarios", R.drawable.icn_usuarios_seleccionado, R.drawable.icn_usuarios),
    PROFILE("Perfil", R.drawable.icn_perfil_seleccionado, R.drawable.icn_perfil)
}

@Composable
private fun TaskColabBottomBar(
    selectedRoute: ReportsNavItem,
    onBoards: () -> Unit,
    onTasks: () -> Unit,
    onReports: () -> Unit,
    onUsers: () -> Unit,
    onProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabBlue)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavButton(ReportsNavItem.BOARDS, selectedRoute == ReportsNavItem.BOARDS, onBoards)
        BottomNavButton(ReportsNavItem.TASKS, selectedRoute == ReportsNavItem.TASKS, onTasks)
        BottomNavButton(ReportsNavItem.REPORTS, selectedRoute == ReportsNavItem.REPORTS, onReports)
        BottomNavButton(ReportsNavItem.USERS, selectedRoute == ReportsNavItem.USERS, onUsers)
        BottomNavButton(ReportsNavItem.PROFILE, selectedRoute == ReportsNavItem.PROFILE, onProfile)
    }
}

@Composable
private fun BottomNavButton(
    item: ReportsNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(58.dp)
            .then(
                if (selected) {
                    Modifier.background(TaskColabWhite, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = if (selected) item.selectedIcon else item.unselectedIcon),
            contentDescription = item.label,
            colorFilter = if (!selected && item == ReportsNavItem.BOARDS) {
                ColorFilter.tint(TaskColabWhite)
            } else {
                null
            },
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) TaskColabBlue else TaskColabWhite,
            maxLines = 1
        )
    }
}

private fun exportSystemStatsPdf(context: Context, stats: ReportStats): ExportedPdf {
    val document = PdfDocument()
    return try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "taskcolab_reporte_$timestamp.pdf"
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.rgb(0, 59, 255)
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.BLACK
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.rgb(65, 65, 65)
            textSize = 14f
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 10f
            strokeCap = Paint.Cap.ROUND
        }

        canvas.drawText("TaskColab - Reporte del sistema", 48f, 70f, titlePaint)
        canvas.drawText(
            "Generado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}",
            48f,
            98f,
            bodyPaint
        )

        canvas.drawText("Estadisticas generales", 48f, 150f, headingPaint)
        canvas.drawText("Total de tareas: ${stats.totalTasks}", 64f, 182f, bodyPaint)
        canvas.drawText("Pendientes: ${stats.pending}", 64f, 208f, bodyPaint)
        canvas.drawText("En proceso: ${stats.inProgress}", 64f, 234f, bodyPaint)
        canvas.drawText("Completadas: ${stats.completed}", 64f, 260f, bodyPaint)
        canvas.drawText("Tableros registrados: ${stats.boards}", 64f, 286f, bodyPaint)
        canvas.drawText("Usuarios activos: ${stats.activeUsers}", 64f, 312f, bodyPaint)

        canvas.drawText("Progreso por tablero", 48f, 370f, headingPaint)
        var y = 406f
        stats.progress.forEach { progress ->
            canvas.drawText("${progress.label} - ${progress.percent}%", 64f, y, bodyPaint)
            linePaint.color = AndroidColor.rgb(217, 217, 217)
            canvas.drawLine(64f, y + 18f, 520f, y + 18f, linePaint)
            linePaint.color = progress.color.toAndroidArgb()
            canvas.drawLine(64f, y + 18f, 64f + (456f * progress.percent / 100f), y + 18f, linePaint)
            y += 58f
        }

        document.finishPage(page)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePdfToDownloads(context, document, fileName)
        } else {
            savePdfToLegacyDownloads(document, fileName)
        }
    } finally {
        document.close()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun savePdfToDownloads(
    context: Context,
    document: PdfDocument,
    fileName: String
): ExportedPdf {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/TaskColab")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: error("No se pudo crear el archivo PDF")

    try {
        resolver.openOutputStream(uri)?.use { output ->
            document.writeTo(output)
        } ?: error("No se pudo abrir el archivo PDF")

        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    } catch (exception: Exception) {
        resolver.delete(uri, null, null)
        throw exception
    }

    return ExportedPdf(fileName = fileName, location = "Descargas/TaskColab")
}

private fun savePdfToLegacyDownloads(document: PdfDocument, fileName: String): ExportedPdf {
    val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "TaskColab"
    )
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, fileName)
    FileOutputStream(file).use { output ->
        document.writeTo(output)
    }
    return ExportedPdf(fileName = fileName, location = "Descargas/TaskColab")
}

private fun Color.toAndroidArgb(): Int {
    return AndroidColor.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}

private data class ReportStats(
    val totalTasks: Int,
    val inProgress: Int,
    val pending: Int,
    val completed: Int,
    val boards: Int,
    val activeUsers: Int,
    val progress: List<ReportProgress>
)

private data class ReportProgress(
    val label: String,
    val percent: Int,
    val color: Color
)

private data class ExportedPdf(
    val fileName: String,
    val location: String
)

private fun buildProgressDistribution(
    pending: Int,
    inProgress: Int,
    completed: Int
): List<ReportProgress> {
    val total = pending + inProgress + completed
    if (total == 0) {
        return listOf(
            ReportProgress("Pendiente", 0, Color(0xFFFF1010)),
            ReportProgress("En proceso", 0, Color(0xFFE88A00)),
            ReportProgress("Completado", 0, Color(0xFF23D624))
        )
    }

    val pendingPercent = pending * 100 / total
    val inProgressPercent = inProgress * 100 / total
    val completedPercent = 100 - pendingPercent - inProgressPercent

    return listOf(
        ReportProgress("Pendiente", pendingPercent, Color(0xFFFF1010)),
        ReportProgress("En proceso", inProgressPercent, Color(0xFFE88A00)),
        ReportProgress("Completado", completedPercent, Color(0xFF23D624))
    )
}
