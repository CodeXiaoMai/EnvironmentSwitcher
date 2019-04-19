./gradlew :environmentswitcher:clean :environmentswitcher:build
./gradlew :environmentswitcher:generatePomFileForReleasePublication
./gradlew :environmentswitcher:publishReleasePublicationToMavenLocal
./gradlew :environmentswitcher:bintrayUpload
#./gradlew :environmentswitcher:bintrayUpload -PbintrayUser= -PbintrayKey= -PdryRun=false