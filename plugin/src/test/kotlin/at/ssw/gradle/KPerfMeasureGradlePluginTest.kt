package at.ssw.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KPerfMeasureGradlePluginTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply("java") // how to apply kotlin plugin?
        project.plugins.apply("at.ssw.k-perf-measure-plugin")
    }

    @Test
    fun `plugin do not find task without registration`() {
        val project = ProjectBuilder.builder().build()

        assertNull(project.tasks.findByName("kPerfMeasureInfo"))
    }

    @Test
    fun `plugin find task after registration with plugin id`() {
        val task = project.tasks.findByName("kPerfMeasureInfo")
        assertNotNull(task)
    }

    @Test
    fun `build task executes successfully`() {
        // Get the build task
        val buildTask = project.tasks.findByName("build")
        assertNotNull(buildTask, "Build task should exist")

        // Execute the build task
        // TODO: Currently no actions, probably because no source files are added?
        buildTask?.actions?.forEach { action ->
            println(action)
            action.execute(buildTask)
        }

        // Verify the build task executed successfully
        assertTrue(buildTask?.state?.executed ?: false, "Build task should have been executed")
        assertTrue(buildTask?.state?.didWork ?: false, "Build task should have performed work")
    }
}
