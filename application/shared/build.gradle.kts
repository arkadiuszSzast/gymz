dependencies {
    testImplementation(testFixtures(project(":application:test-utils")))

    testFixturesImplementation(libs.kotest.extra.arb)
}