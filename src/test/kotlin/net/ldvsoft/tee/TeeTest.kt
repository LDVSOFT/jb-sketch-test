package net.ldvsoft.tee

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class TeeTest {
    private fun checkFiles(expected: Path, actual: Path) {
        val expectedBytes = Files.readAllBytes(expected)
        val actualBytes = Files.readAllBytes(actual)
        assertArrayEquals(expectedBytes, actualBytes)
    }

    private fun testTee(
            mode: WriteMode,
            input: Path,
            output: Path,
            saves: List<Pair<Path, Path>>
    ) {
        val config = TeeConfig(
                mode,
                Files.newInputStream(input),
                Files.newOutputStream(output),
                saves.map { it.first }
        )
        tee(config)
        checkFiles(input, output)
        saves.forEach { (targetFile, expectedFile) ->
            checkFiles(expectedFile, targetFile)
        }
    }

    @Test
    @DisplayName("Simple Test")
    fun simpleTest() {
        val inputPath = Paths.get( "src", "test", "resources", "example.txt")
        fun createTempFile() = Files.createTempFile("simple-tee-test", ".txt")
        val outputPath = createTempFile()
        val testPath1 = createTempFile  ()
        val testPath2 = createTempFile   ()
        val testPath3 = createTempFile    ()
        val testPath4 = createTempFile     ()

        val doubledInputPath = createTempFile()
        val input = Files.readAllBytes(inputPath)
        Files.write(doubledInputPath, input)
        Files.write(doubledInputPath, input, StandardOpenOption.APPEND)

        testTee(
                WriteMode.OVERWRITE,
                inputPath,
                outputPath,
                listOf(testPath1 to inputPath, testPath2 to inputPath)
        )

        testTee(
                WriteMode.OVERWRITE,
                inputPath,
                outputPath,
                listOf(testPath1 to inputPath, testPath3 to inputPath)
        )

        testTee(
                WriteMode.APPEND,
                inputPath,
                outputPath,
                listOf(testPath1 to doubledInputPath, testPath4 to inputPath)
        )
    }
}