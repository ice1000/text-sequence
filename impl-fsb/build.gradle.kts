plugins { java }

sourceSets {
	main { java.setSrcDirs(listOf("src")) }
	test { java.setSrcDirs(listOf("test"))	}
}

dependencies {
	implementation(project(":impl-gap"))
	testImplementation(project(":test-common"))
}
