pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
//        maven { url = uri("https://jitpack.io") }
//        flatDir {
//            dir("libs")
//        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
//        flatDir {
//            dir("libs")
//        }
    }
}

rootProject.name = "Bar code Scanner"
include(":app")
include(":qrCodeScanner")
