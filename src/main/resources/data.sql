INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (1, 'Jan', 'Kowalski', 'Fizyka', 1);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (2, 'Ewa', 'Kowalska', 'Astronomia', 2);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (3, 'Krystian', 'Brodaty', 'Informatyka', 1);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (4, 'Agata', 'Herbata', 'Fizyka', 1);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (5, 'Tomasz', 'Plecak', 'Fizyka', 2);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (6, 'Ewelina', 'Lina', 'Informatyka', 1);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (7, 'Eustachy', 'Rzeka', 'Fizyka', 2);
INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES (8, 'Eustachiusz', 'Rzeka', 'Fizyka', 2);

INSERT INTO teachers (teacher_id, first_name, last_name) VALUES (1, 'Marian', 'Jezioro'), (2, 'Joanna', 'Talerz'), (3, 'Edmund', 'Gruszka'), (4, 'Teacher', 'Teacher'), (5, 'Teacher2', 'Teacher2'), (6, 'Teacher3', 'Teacher3');

INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (1, 'student', 'student', 'STUDENT', true, NULL, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (2, 'teacher', 'teacher', 'TEACHER', true, NULL, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (3, 'admin', 'admin', 'ADMIN', true, NULL, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (4, 'JanKowalski@uwr.edu.pl', '123456', 'STUDENT', true, 1, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (5, 'Ewa.Kowalska', '123456', 'STUDENT', true, 2, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (6, 'Krystian.Brodaty', '123456', 'STUDENT', true, 3, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (7, 'Agata.Herbata', '123456', 'STUDENT', true, 4, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (8, 'Tomasz.Plecak', '123456', 'STUDENT', true, 5, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (9, 'Ewelina.Lina', '123456', 'STUDENT', true, 6, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (10, 'Eustachy.Rzeka', '123456', 'STUDENT', true, 7, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (11, 'Eustachiusz.Rzeka', '123456', 'STUDENT', true, 8, NULL);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (12, 'Marian.Jezioro', '123456', 'TEACHER', true, NULL, 1);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (13, 'Joanna.Talerz', '123456', 'TEACHER', true, NULL, 2);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (14, 'Edmund.Gruszka', '123456', 'TEACHER', true, NULL, 3);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (15, 'Teacher.Teacher', '123456', 'TEACHER', true, NULL, 4);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (16, 'Teacher2.Teacher2', '123456', 'TEACHER', true, NULL, 5);
INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES (17, 'Teacher3.Teacher3', '123456', 'TEACHER', true, NULL, 6);

INSERT INTO `courses` VALUES (1,5,'Analiza matematyczna 1'),(2,3,'Podstawy programowania'),(3,4,'Matematyka dyskretna');

INSERT INTO `solutions` VALUES (1,'Jan solution 1','2019-11-23 14:12:12',_binary '',5,'solution.1.800px-MVC-Process.svg.png',1,1),(2,'Jan solution 2','2019-11-23 14:12:18',_binary '',3,'solution.2.controller.png',1,1),(3,'Jan solution2 1','2019-11-23 14:12:23',_binary '',4.5,'solution.3.dao.png',1,1),(4,'Jan solution2 2','2019-11-23 14:12:29',_binary '',3.5,'solution.4.data.png',2,1),(5,'Ewa solution 1','2019-11-23 14:12:34',_binary '',4,'solution.5.Java-EE-vs-Spring-1.jpg',2,2);

INSERT INTO `assignments` VALUES (1,'Zaimplementuj 3 dowolne metody sortowania','assignment.3.800px-MVC-Process.svg','Wstep do sortowania',1,NULL,NULL),(2,'Content','assignment.4.1200px-MySQL.svg','Title',1,NULL,NULL);

INSERT INTO `file_history` VALUES (1,'800px-MVC-Process.svg.png','Solution','2019-11-23 14:12:12','solution.1.800px-MVC-Process.svg.png',1),(2,'controller.png','Solution','2019-11-23 14:12:18','solution.2.controller.png',2),(3,'dao.png','Solution','2019-11-23 14:12:23','solution.3.dao.png',3),(4,'data.png','Solution','2019-11-23 14:12:29','solution.4.data.png',4),(5,'Java-EE-vs-Spring-1.jpg','Solution','2019-11-23 14:12:34','solution.5.Java-EE-vs-Spring-1.jpg',5);