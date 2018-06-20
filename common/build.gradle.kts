plugins { java }
java.sourceSets { "main" { java.setSrcDirs(listOf("src")) } }
dependencies {
	compile(group = "org.jetbrains", name = "annotations", version = "16.0.1")
	testCompile(":test-common")
	testCompile(group = "junit", name = "junit", version = "4.12")
}
