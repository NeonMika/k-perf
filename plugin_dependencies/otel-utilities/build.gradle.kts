plugins {
    base
}

tasks.named("clean") {
    dependsOn(":util:clean", ":util-proto:clean")
}

tasks.register("publishToMavenLocal") {
    group = "publishing"
    description = "Publishes both OTel utility variants to Maven Local."
    dependsOn(":util:publishToMavenLocal", ":util-proto:publishToMavenLocal")
}
