package net.ldvsoft.tee

import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun tee(config: TeeConfig) {
    val openOptions = when (config.writeMode) {
        WriteMode.OVERWRITE -> emptyArray()
        WriteMode.APPEND    -> arrayOf(StandardOpenOption.APPEND)
    }
    val fileWriters = config.files
            .map { Files.newBufferedWriter(it, *openOptions) }
            .toTypedArray()
    val writers = listOf(config.output.writer(), *fileWriters)
    val inputReader = config.input.reader()
    val buffer = CharArray(DEFAULT_BUFFER_SIZE)

    try {
        while (true) {
            val readBytes = inputReader.read(buffer)
            if (readBytes == -1) {
                // End of file!
                break
            }
            writers.forEach { it.write(buffer, 0, readBytes) }
        }
    } finally {
        inputReader.close()
        writers.forEach { it.close() }
    }
}