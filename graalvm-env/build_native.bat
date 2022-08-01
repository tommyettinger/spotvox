"C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvarsall.bat" x86_amd64 && ^
C:\d\jvm\graal17\bin\native-image.cmd ^
-H:+ReportExceptionStackTraces ^
--report-unsupported-elements-at-runtime ^
--no-fallback ^
--allow-incomplete-classpath ^
-H:ReflectionConfigurationFiles=config/reflect-config.json ^
-H:JNIConfigurationFiles=config/jni-config.json ^
-H:DynamicProxyConfigurationFiles=config/proxy-config.json ^
-H:SerializationConfigurationFiles=config/serialization-config.json ^
-H:ResourceConfigurationFiles=config/resource-config.json ^
-Dorg.lwjgl.librarypath=. ^
-jar spotvox.jar