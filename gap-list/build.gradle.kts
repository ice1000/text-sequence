plugins { java }

java.sourceSets {
	"main" { java.setSrcDirs(listOf("src")) }
	"test" { java.setSrcDirs(listOf("test")) }
}

repositories { jcenter() }

dependencies {
	testCompile(group = "junit", name = "junit", version = "4.12")
	testCompile(group = "org.hamcrest", name = "hamcrest-library", version = "1.3")
}
