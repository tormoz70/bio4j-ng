mvn deploy:deploy-file -Dfile=ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0.1 -DgeneratePom=true -Dpackaging=jar -DrepositoryId=nexus-deployment -Durl=http://5.8.177.199:8081/repository/3d-party/
