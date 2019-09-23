# StudentManager
Engineering degree project

# How to run application on your pc?
1) Install IntelliJ IDEA Community Edition
2) Clone repository into your workspace
3) File -> Open -> StudentManager/pom.xml and open as project
4) Run application (Shift + F10)
5) Application should be available under http://localhost:8080/

* Before first launch go to application.properties and change
```
spring.jpa.hibernate.ddl-auto = validate
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
