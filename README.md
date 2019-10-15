# StudentManager
Engineering degree project

# How to run application on your pc?
1) Install IntelliJ IDEA Community Edition
2) Clone repository into your workspace
3) Download Java SE Development Kit 8u221 from https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
4) Extract tar content on disk
5) Set JAVA_HOME to this location
6) Run "./mvnw clean install" inside StudentManager folder
7) If code compiled successfully, go to /target dir
8) Rename ROOT-2.1.6.RELEASE.war to ROOT.war
9) Copy ROOT.war to apache-tomcat/webapps folder (Remove ROOT folder if it already exists).
10) Go to apache-tomcat/bin and run starup
11) Application should be available under http://localhost:8080/

* Before first launch go to application.properties and change
```
spring.jpa.hibernate.ddl-auto = update
```
to
```
spring.jpa.hibernate.ddl-auto = create
```

6) To log in use one of 3 available users:
- admin
- teacher
- student

7) For now, password is the same as login (for example login = student, password = student)
