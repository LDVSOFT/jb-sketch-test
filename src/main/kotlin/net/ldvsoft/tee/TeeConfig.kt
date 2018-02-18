package net.ldvsoft.tee

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path

enum class WriteMode {
    OVERWRITE,
    APPEND
}

data class TeeConfig(
        val writeMode: WriteMode,
        val input: InputStream,
        val output: OutputStream,
        val files: List<Path>
)