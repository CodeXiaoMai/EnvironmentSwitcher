./gradlew :compiler-release:clean :compiler-release:build
./gradlew :compiler-release:generatePomFileForMavenPublication
./gradlew :compiler-release:publishMavenPublicationToMavenLocal
./gradlew :base:bintrayUpload
#./gradlew :compiler-release:bintrayUpload -PbintrayUser= -PbintrayKey= -PdryRun=false