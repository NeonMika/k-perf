package at.ssw.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KIRVisualizerGradlePluginTest {

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply("java") // how to apply kotlin plugin?
        project.plugins.apply("at.ssw.k-ir-visualizer-plugin")
    }

    @Test
    fun `plugin do not find task without registration`() {
        val project = ProjectBuilder.builder().build()

        assertNull(project.tasks.findByName("kIRVisualizerInfo"))
    }

    @Test
    fun `plugin find task after registration with plugin id`() {
        val task = project.tasks.findByName("kIRVisualizerInfo")
        assertNotNull(task)
    }
}
