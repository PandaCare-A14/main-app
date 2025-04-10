plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "6.0.1.5171"
}

group = "com.pandacare"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

sonar {
	properties {
		property("sonar.projectKey", "PandaCare-A14_main-app")
		property("sonar.organization", "pandacare-a14")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.token", System.getenv("SONAR_TOKEN"))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
