dependencies {
//    implementation("com.github.taes-k:mock-sut:$version")
//    annotationProcessor("com.github.taes-k:mock-sut:$version")
    implementation(project(":annotation-processor"))
    annotationProcessor(project(":annotation-processor"))

    implementation("org.mockito:mockito-all:1.10.19")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-collections4:4.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}