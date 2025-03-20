import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NexusReplication {
    private val stateFile = File("file_state.txt")

    // Метод для загрузки предыдущего состояния (время последнего изменения файлов)
    private fun loadPreviousState(): Map<String, Long> {
        val previousState = mutableMapOf<String, Long>()

        if (stateFile.exists()) {
            stateFile.forEachLine {
                val parts = it.split(",")
                if (parts.size == 2) {
                    val fileName = parts[0]
                    val lastModified = parts[1].toLong()
                    previousState[fileName] = lastModified
                }
            }
        }
        return previousState
    }

    // Метод для сохранения текущего состояния в файл
    private fun saveCurrentState(files: List<File>) {
        stateFile.printWriter().use { writer ->
            files.forEach { file ->
                writer.println("${file.name},${file.lastModified()}")
            }
        }
    }

    fun checkNewDBBackup() {
        // Получаем текущую дату в формате yyyy-dd-HH-mm-ss
        val dateFormat = SimpleDateFormat("yyyy-dd-HH-mm-ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Формируем путь к файлу
        val filePath = "/backup/nexus/nexus-$currentDate.zip"

        // Проверка наличия файла
        val file = File(filePath)
        if (file.exists()) {
            println("Файл $filePath существует.")
        } else {
            println("Файл $filePath не найден.")
        }
    }

    fun checkChangesOfBlobs() {
        // Путь к директории blobs
        val blobsDir = File("/opt/sonatype-nexus/nexus3/blobs")

        // Проверка, существует ли директория
        if (!blobsDir.exists() || !blobsDir.isDirectory) {
            println("Директория $blobsDir не существует или это не директория.")
            return
        }

        // Получаем список файлов в директории
        val files = blobsDir.listFiles()

        if (files != null) {
            val previousState = loadPreviousState()

            for (file in files) {
                val lastModified = file.lastModified()

                // Если файл был изменён
                if (previousState[file.name] != lastModified) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(lastModified))
                    println("Файл ${file.name} был изменен: $formattedDate")
                }
            }

            // Сохраняем новое состояние
            saveCurrentState(files.toList())
        } else {
            println("Не удалось получить список файлов в директории $blobsDir.")
        }
    }
}
