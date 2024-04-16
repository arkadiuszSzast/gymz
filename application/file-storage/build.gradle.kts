dependencies {
    api(libs.aws.s3)
    implementation(libs.apache.tika)

    implementation(project(":application:shared"))
    testImplementation(testFixtures(project(":application:test-utils")))

    testFixturesImplementation(libs.testcontainers)
}