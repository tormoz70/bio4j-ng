"%JAVA_HOME%\bin\java" ^
-server ^
-Dlogback.configurationFile=conf/logback.xml ^
-XX:-UseParallelGC ^
-XX:MaxPermSize=256M ^
-Xms256M -Xmx1G ^
-Dcom.sun.management.jmxremote.port=1100 ^
-Dcom.sun.management.jmxremote.authenticate=false ^
-Dcom.sun.management.jmxremote.ssl=false ^
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9009 ^
-Dgosh.args=--noi ^
-jar bin/felix.jar