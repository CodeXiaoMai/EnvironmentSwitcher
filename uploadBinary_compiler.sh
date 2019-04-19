./gradlew :compiler:clean :compiler:build
./gradlew :compiler:generatePomFileForMavenPublication
./gradlew :compiler:publishMavenPublicationToMavenLocal
./gradlew :base:bintrayUpload
#./gradlew :compiler:bintrayUpload -PbintrayUser= -PbintrayKey= -PdryRun=false