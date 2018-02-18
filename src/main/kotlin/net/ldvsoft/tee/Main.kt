package net.ldvsoft.tee

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Paths

fun parseOptions(
        args: Array<String>,
        input: InputStream = System.`in`,
        output: OutputStream = System.out
): TeeConfig {
    var optionsEnded = false
    var append = false
    val files = mutableListOf<String>()

    for (arg in args) {
        if (!arg.startsWith("-") || optionsEnded) {
            optionsEnded = true
            files.add(arg)
        } else {
            val letters = arg.removePrefix("-")
            for (option in letters) {
                when (option) {
                    'a' -> append = true
                    else -> throw IllegalArgumentException("Unknown option: -$option")
                }
            }
        }
    }
    return TeeConfig(
            if (append) WriteMode.APPEND else WriteMode.OVERWRITE,
            input,
            output,
            files.map { Paths.get(it) }
    )
}

fun main(args: Array<String>) {
    try {
        tee(parseOptions(args))
    } catch (e: Exception) {
        System.err.println("Error ${e.javaClass.name}: ${e.message}")
    }
}