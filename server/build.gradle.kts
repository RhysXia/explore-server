dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("com.graphql-java:graphql-java-extended-scalars:2022-04-28T01-36-15-1764ea8")
  implementation(project(":spring-boot-starter-graphql"))
  runtimeOnly("io.r2dbc:r2dbc-postgresql")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.r2dbc:r2dbc-h2")
  testImplementation("io.projectreactor:reactor-test")
}