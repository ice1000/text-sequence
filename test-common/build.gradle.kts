plugins { java }
java.sourceSets {
	"main" { java.setSrcDirs(listOf("src")) }
	"test" { java.setSrcDirs(listOf("test")) }
}
repositories { jcenter() }
dependencies {
	compile(project(":common"))
	compile(group = "junit", name = "junit", version = "4.12")
}
