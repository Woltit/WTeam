package com.wteam.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class TodoPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("todoPlugin", Extension::class.java)

        target.tasks.register("codeAnalysis", TodoAnalysisTask::class.java) { task ->
            task.group = "code analysis"
            task.description = "Find all todos and fixme comments and write all data into file"

            task.pluginEnabled.convention(extension.enabled.orElse(true))

            target.plugins.withId("java") {
                val java = target.extensions.getByType(JavaPluginExtension::class.java)
                task.sourceFiles.from(java.sourceSets.getByName("main").allSource)
            }

            task.outputDir.convention(
                target.layout.buildDirectory.dir("generated/sources/todo-plugin")
            )
        }
    }
}
