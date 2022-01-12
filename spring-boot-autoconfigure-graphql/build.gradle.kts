dependencies {
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework:spring-webflux")
  implementation("org.springframework.boot:spring-boot-starter-websocket")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  api("com.graphql-java:graphql-java:17.3")
}
