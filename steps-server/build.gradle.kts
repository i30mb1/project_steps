plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.21"
}

dependencies {
    implementation("io.ktor:ktor-server-netty:3.2.3")
    implementation("io.ktor:ktor-server-websockets:3.2.3")
//    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("io.ktor:ktor-client-cio:3.2.3")
    implementation("io.obs-websocket.community:client:2.0.0")
}