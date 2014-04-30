"%JAVA_HOME%\bin\java" ^
-server ^
-XX:-UseParallelGC ^
-XX:MaxPermSize=256M ^
-Xms256M -Xmx1G ^
-Dcom.sun.management.jmxremote.port=1100 ^
-Dcom.sun.management.jmxremote.authenticate=false ^
-Dcom.sun.management.jmxremote.ssl=false ^
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9009 ^
-Dgosh.args=--noi ^
-jar bin/felix.jar

rem -Dlogback.configurationFile=conf/logback.xml ^
rem -Dlog4j.configurationFile=conf/org.ops4j.pax.logging.cfg ^
