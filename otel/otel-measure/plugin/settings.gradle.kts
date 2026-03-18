pluginManagement{
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
            credentials {
                username = providers.gradleProperty("GITHUB_USERNAME").get()
                password = providers.gradleProperty("GITHUB_USERNAME").get()
            }
        }
        mavenLocal()
        mavenCentral()
    }
}

rootProject.name = "plugin"
