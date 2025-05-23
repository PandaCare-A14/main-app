plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.sonarqube") version "4.3.1.3277"
}

group = "com.pandacare"
version = "0.0.1-SNAPSHOT"

val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

sonar {
	properties {
		property("sonar.projectKey", "noQils_eshop")
		property("sonar.organization", "noqils")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.token", System.getenv("SONAR_TOKEN"))
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("org.postgresql:postgresql:42.7.1")
	runtimeOnly ("org.postgresql:postgresql")
	runtimeOnly ("com.h2database:h2")
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
	implementation ("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation ("org.hibernate.validator:hibernate-validator:8.0.0.Final")
	implementation ("io.micrometer:micrometer-registry-prometheus")
	implementation ("org.springframework.boot:spring-boot-starter-actuator")
	implementation ("org.springframework.boot:spring-boot-starter-security")
	implementation ("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.apache.httpcomponents:httpclient:4.5.14")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("me.paulschwarz:spring-dotenv:2.2.0")
	testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
	testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
	testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.register<Test>("unitTest") {
	description = "Runs the unit tests."
	group = "verification"

	filter {
		excludeTestsMatching("*FunctionalTest")
	}
}

tasks.register<Test>("functionalTest") {
	description = "Runs functional tests."
	group = "verification"

	filter {
		includeTestsMatching("*FunctionalTest")
	}
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

tasks.test {
	filter {
		excludeTestsMatching("*FunctionalTest")
	}

	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		csv.required = false
		html.required = true
	}
}