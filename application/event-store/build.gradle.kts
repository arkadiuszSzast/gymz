dependencies {
    implementation(project(":application:shared"))
    api(libs.eventstore)

    testFixturesImplementation(libs.testcontainers)
    testFixturesImplementation(libs.koin.test)
    testFixturesImplementation(libs.kotest.framework.api)
}
