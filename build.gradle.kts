import com.jfrog.bintray.gradle.*

buildscript {
	repositories { jcenter() }
	dependencies { classpath("com.palantir:jacoco-coverage:0.4.0") }
}

plugins {
	java
	jacoco
	`maven-publish`
	id("com.jfrog.bintray") version "1.7.3"
}

var isCI: Boolean by extra
isCI = !System.getenv("CI").isNullOrBlank()

allprojects {
	group = "org.ice1000.textseq"
	version = "v0.4"

	apply {
		plugin("java")
		plugin("jacoco")
		plugin("com.palantir.jacoco-full-report")
	}

	repositories {
		mavenCentral()
		jcenter()
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
		from(java.sourceSets["main"].allSource)
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
		(publications) {
			"mavenJava"(MavenPublication::class) {
				from(components["java"])
				groupId = project.group.toString()
				artifactId = "${rootProject.name}-${project.name}"
				version = project.version.toString()
				artifact(tasks["sourcesJar"])
				pom.withXml {
					val root = asNode()
					root.appendNode("description", "Text sequence data structures")
					root.appendNode("name", project.name)
					root.appendNode("url", githubUrl)
					root.children().last()
				}
			}
		}
	}
}
