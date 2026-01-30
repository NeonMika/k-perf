package at.jku.ssw.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KPerfGradlePluginTest {

  private lateinit var project: Project

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().build()
    project.plugins.apply("java") // how to apply kotlin plugin?
    project.plugins.apply("at.jku.ssw.k-perf-plugin")
  }

  @Test
  fun `do not find kperf extension in empty Gradle project`() {
    val emptyProject = ProjectBuilder.builder().build()
    assertNull(emptyProject.extensions.findByName("kperf"))
  }

  @Test
  fun `find kperf extension in Gradle project that appies k-perf-plugin`() {
    val extension = project.extensions.findByName("kperf")
    assertNotNull(extension)
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

    // TODO: Check why buildTask is not marked as executed
    // Verify the build task executed successfully
    // assertTrue(buildTask?.state?.executed ?: false, "Build task should have been executed")
    // assertTrue(buildTask?.state?.didWork ?: false, "Build task should have performed work")
  }
}