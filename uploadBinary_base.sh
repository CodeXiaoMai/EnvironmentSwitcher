./gradlew :base:clean :base:build
./gradlew :base:generatePomFileForMavenPublication
./gradlew :base:publishMavenPublicationToMavenLocal
./gradlew :base:bintrayUpload
#./gradlew :base:bintrayUpload -PbintrayUser= -PbintrayKey= -PdryRun=false