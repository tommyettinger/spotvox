%HOME%\.jdks\graalvm-jdk-24.36.1\bin\native-image.cmd ^
-march=native ^
--no-fallback ^
--enable-native-access=ALL-UNNAMED ^
-jar ../headless/build/libs/spotvox.jar