import com.jfrog.bintray.gradle.*

plugins {
	java
	jacoco
	`maven-publish`
	id("com.jfrog.bintray") version "1.8.4"
}

var isCI: Boolean by extra
isCI = !System.getenv("CI").isNullOrBlank()

allprojects {
	group = "org.ice1000.textseq"
	version = "v0.4"

	apply {
		plugin("java")
		plugin("jacoco")
	}

	repositories {
		mavenCentral()
		jcenter()
	}

	dependencies {
		implementation(group = "org.jetbrains", name = "annotations", version = "19.0.0")
		testImplementation(group = "junit", name = "junit", version = "4.12")
		testImplementation(group = "org.hamcrest", name = "hamcrest-library", version = "1.3")
	}

	tasks.withType<JavaCompile> {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"
		options.apply {
			isDeprecation = true
			isWarnings = true
			isDebug = !isCI
			compilerArgs.add("-Xlint:unchecked")
		}
	}

	val sourcesJar = task<Jar>("sourcesJar") {
		group = tasks["jar"].group
		from(sourceSets["main"].allSource)
		classifier = "sources"
	}

	artifacts { add("archives", sourcesJar) }
}

val githubUrl = "https://github.com/ice1000/text-sequence"

subprojects {
	if (project.name == "test-common") return@subprojects

	apply {
		plugin("maven")
		plugin("maven-publish")
		plugin("com.jfrog.bintray")
	}

	bintray {
		user = "ice1000"
		key = findProperty("key").toString()
		setConfigurations("archives")
		pkg.apply {
			name = "textseq"
			repo = "ice1000"
			githubRepo = "ice1000/text-sequence"
			publicDownloadNumbers = true
			vcsUrl = "$githubUrl.git"
			version.apply {
				vcsTag = "${project.version}"
				name = vcsTag
				websiteUrl = "$githubUrl/releases/tag/$vcsTag"
			}
		}
	}

	publishing {
		publications {
			create<MavenPublication>("maven") {
				from(components["java"])
				groupId = project.group.toString()
				artifactId = "${rootProject.name}-${project.name}"
				version = project.version.toString()
				artifact(tasks["sourcesJar"])
				pom {
					description.set("Text sequence data structures")
					name.set(project.name)
					url.set(githubUrl)
					licenses {
						license {
							name.set("The Apache License, Version 2.0")
							url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
						}
					}
					developers {
						developer {
							id.set("ice1000")
							name.set("Tesla Ice Zhang")
							email.set("ice1000kotlin@foxmail.com")
						}
					}
				}
			}
		}
	}
}
