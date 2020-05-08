sourceSets {
	main { java.setSrcDirs(listOf("src")) }
	test { java.setSrcDirs(listOf("test"))	}
}

dependencies {
	implementation(project(":common"))
	implementation(group = "junit", name = "junit", version = "4.12")
	implementation(group = "org.hamcrest", name = "hamcrest-library", version = "1.3")
}
