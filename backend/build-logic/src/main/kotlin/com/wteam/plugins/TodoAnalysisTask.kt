package com.wteam.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import java.util.regex.Pattern

abstract class TodoAnalysisTask : DefaultTask() {

    companion object {
        private val TODO_PATTERN: Pattern = Pattern.compile("\\btodo\\b", Pattern.CASE_INSENSITIVE)
        private val FIXME_PATTERN: Pattern = Pattern.compile("\\bfixme\\b", Pattern.CASE_INSENSITIVE)

        private const val METADATA_PATH: String = "metadata.txt"
        private const val CODE_ANALYSIS_DATA_PATH: String = "analysis.txt"
    }

    enum class TypeOfComment {
        TODO, FIXME
    }

    data class TodoItem(
        val type: TypeOfComment,
        val filepath: String,
        val lineNumber: Int,
        val commentText: String
    )

    @get:Input
    abstract val pluginEnabled: Property<Boolean>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        if (!pluginEnabled.get()) {
            logger.lifecycle("TodoPlugin task is disabled!")
            return
        }

        val outDir = outputDir.get().asFile
        outDir.mkdirs()

        var totalLines = 0
        var emptyLines = 0
        var totalFiles = 0
        val results = mutableListOf<TodoItem>()

        sourceFiles.forEach { file ->
            if (file.isFile && (file.extension == "java" || file.extension == "kt")) {
                var isInMultiLineComment = false

                file.readLines().forEachIndexed { index, line ->
                    val trimmed = line.trim()

                    if (trimmed.isBlank()) {
                        emptyLines++
                    } else {
                        when {
                            !isInMultiLineComment && trimmed.contains("/*") -> {
                                isInMultiLineComment = !trimmed.contains("*/")
                                extractCommentPart(line)?.let { comment ->
                                    processComment(comment, index + 1, file, results)
                                }
                            }

                            isInMultiLineComment -> {
                                val comment = trimmed
                                    .removePrefix("*")
                                    .removeSuffix("*/")
                                    .trim()

                                processComment(comment, index + 1, file, results)

                                if (trimmed.contains("*/")) {
                                    isInMultiLineComment = false
                                }
                            }

                            trimmed.contains("//") -> {
                                extractCommentPart(line)?.let { comment ->
                                    processComment(comment, index + 1, file, results)
                                }
                            }
                        }
                    }

                    totalLines++
                }

                totalFiles++
            }
        }

        val reportFile = File(outDir, CODE_ANALYSIS_DATA_PATH)
        val reportContent = results.joinToString("\n") {
            "${it.type}: ${it.commentText} (File: ${it.filepath}, Line: ${it.lineNumber})"
        }
        reportFile.writeText(reportContent)

        val metadataFile = File(outDir, METADATA_PATH)
        metadataFile.writeText(
            """
            Total Files: $totalFiles
            Total Lines: $totalLines
            Empty Lines: $emptyLines
            Total Items Found: ${results.size}
            """.trimIndent()
        )

        logger.lifecycle("Found ${results.size} items. Report: ${reportFile.absolutePath}")
    }

    private fun extractCommentPart(line: String): String? {
        return when {
            line.contains("//") -> line.substringAfter("//").trim()
            line.contains("/*") -> line.substringAfter("/*").substringBefore("*/").trim()
            else -> null
        }
    }

    private fun processComment(
        comment: String,
        lineNumber: Int,
        file: File,
        results: MutableCollection<TodoItem>
    ) {
        val relativePath = file.relativeTo(project.projectDir).path

        if (TODO_PATTERN.matcher(comment).find()) {
            results.add(
                TodoItem(
                    TypeOfComment.TODO,
                    relativePath,
                    lineNumber,
                    extractMessage(comment, "todo")
                )
            )
        }

        if (FIXME_PATTERN.matcher(comment).find()) {
            results.add(
                TodoItem(
                    TypeOfComment.FIXME,
                    relativePath,
                    lineNumber,
                    extractMessage(comment, "fixme")
                )
            )
        }
    }

    private fun extractMessage(comment: String, tag: String): String {
        val regex = Regex("(?i).*?\\b$tag\\b\\s*:?\\s*")
        return comment.replaceFirst(regex, "").trim()
    }
}