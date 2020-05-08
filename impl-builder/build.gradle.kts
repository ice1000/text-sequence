sourceSets {
	main { java.setSrcDirs(listOf("src")) }
	test { java.setSrcDirs(listOf("test"))	}
}

dependencies {
	implementation(project(":common"))
	testImplementation(project(":test-common"))
}
