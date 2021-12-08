dependencies {
//    implementation("com.github.taes-k:mock-sut:$version")
//    annotationProcessor("com.github.taes-k:mock-sut:$version")
    implementation(project(":annotation-processor"))
    annotationProcessor(project(":annotation-processor"))

    testImplementation("org.mockito:mockito-all:1.10.19")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-collections4:4.4")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

val generatedSourcePath = "$buildDir/generated/sources/annotationProcessor/java/test"
val generatedOutputDir = file(generatedSourcePath)

tasks.withType<JavaCompile> {
    doFirst {
        generatedOutputDir.exists() || generatedOutputDir.mkdirs()
        options.compilerArgs = listOf("-s", generatedSourcePath)
    }
//    options.annotationProcessorGeneratedSourcesDirectory = generatedOutputDir
//    outputs.dir(generatedSourcePath)
}

sourceSets.main {
    java.exclude(generatedSourcePath + "/**")
}
sourceSets.test {
    java.srcDirs(generatedOutputDir)
}
