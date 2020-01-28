INSERT INTO students (student_id, first_name, last_name, faculty, current_year) VALUES
    (1, 'Jan', 'Kowalski', 'Fizyka', 1),
    (2, 'Ewa', 'Kowalska', 'Astronomia', 2),
    (3, 'Krystian', 'Brodaty', 'Informatyka', 1),
    (4, 'Agata', 'Herbata', 'Fizyka', 1),
    (5, 'Tomasz', 'Plecak', 'Fizyka', 2),
    (6, 'Ewelina', 'Lina', 'Informatyka', 1),
    (7, 'Eustachy', 'Rzeka', 'Fizyka', 2),
    (8, 'Eustachiusz', 'Rzeka', 'Fizyka', 2);

INSERT INTO teachers (teacher_id, first_name, last_name) VALUES
    (1, 'Marian', 'Jezioro'),
    (2, 'Joanna', 'Talerz'),
    (3, 'Edmund', 'Gruszka'),
    (4, 'Teacher', 'Teacher'),
    (5, 'Teacher2', 'Teacher2'),
    (6, 'Teacher3', 'Teacher3');

INSERT INTO users (user_id, username, password, role, enabled, student_id, teacher_id) VALUES
    (1, 'student', 'student', 'STUDENT', true, NULL, NULL),
    (2, 'teacher', 'teacher', 'TEACHER', true, NULL, NULL),
    (3, 'admin', '123456', 'ADMIN', true, NULL, NULL),
    (4, 'JanKowalski@uwr.edu.pl', '123456', 'STUDENT', true, 1, NULL),
    (5, 'EwaKowalska@uwr.edu.pl', '123456', 'STUDENT', true, 2, NULL),
    (6, 'KrystianBrodaty@uwr.edu.pl', '123456', 'STUDENT', true, 3, NULL),
    (7, 'AgataHerbata@uwr.edu.pl', '123456', 'STUDENT', true, 4, NULL),
    (8, 'TomaszPlecak@uwr.edu.pl', '123456', 'STUDENT', true, 5, NULL),
    (9, 'EwelinaLina@uwr.edu.pl', '123456', 'STUDENT', true, 6, NULL),
    (10, 'EustachyRzeka@uwr.edu.pl', '123456', 'STUDENT', true, 7, NULL),
    (11, 'EustachiuszRzeka@uwr.edu.pl', '123456', 'STUDENT', true, 8, NULL),
    (12, 'MarianJezioro@uwr.edu.pl', '123456', 'TEACHER', true, NULL, 1),
    (13, 'JoannaTalerz@uwr.edu.pl', '123456', 'TEACHER', true, NULL, 2),
    (14, 'EdmundGruszka@uwr.edu.pl', '123456', 'TEACHER', true, NULL, 3),
    (15, 'TeacherTeacher@uwr.edu.pl', '123456', 'TEACHER', true, NULL, 4);

CREATE INDEX BUNDLES_IDX ON bundles (bundle, key_value, message);

INSERT INTO courses VALUES
    (1, 5, 'Analiza matematyczna 1'),
    (2, 3, 'Podstawy programowania'),
    (3, 4, 'Matematyka dyskretna'),
    (4, 5, 'Analiza matematyczna 2'),
    (5, 5, 'Algorytmy i struktury danych'),
    (6, 3, 'Elektronika'),
    (7, 4, 'Analiza numeryczna'),
    (8, 4, 'Podstawy fizyki 1'),
    (9, 4, 'Podstawy fizyki 2'),
    (10, 4, 'Podstawy fizyki 3'),
    (11, 4, 'Programowanie w c++'),
    (12, 5, 'Projekt w jezyku c++'),
    (13, 4, 'Zaawansowane programowanie w c++'),
    (14, 4, 'Bazy danych');

INSERT INTO solutions VALUES
    (1,'Jan solution 1','2019-11-23 14:12:12', 1,5,'solution.1.1.png',1,1),
    (2,'Jan solution 2','2019-11-23 14:12:18', 1,3,'solution.2.2.png',1,1),
    (3,'Jan solution2 1','2019-11-23 14:12:23', 1,4.5,'solution.3.3.png',1,1),
    (4,'Jan solution2 2','2019-11-23 14:12:29', 1,3.5,'solution.4.4.png',2,1),
    (5,'Jan solution 4','2019-11-23 14:12:34', 1,4,'solution.5.5.png',3,1),
    (6,'Jan solution 5','2019-11-23 14:12:34', 1,4,'solution.6.6.png',4,1),
    (7,'Jan solution 6','2019-11-23 14:12:34', 1,4,'solution.7.7.png',5,1),
    (8,'Jan solution 4','2019-11-23 14:12:34', 1,4,'solution.5.5.png',6,1),
    (9,'Jan solution 5','2019-11-23 14:12:34', 1,4,'solution.6.6.png',7,1),
    (10,'Jan solution 6','2019-11-23 14:12:34', 1,4,'solution.7.7.png',8,1);

INSERT INTO course_student VALUES
    (1, 1), (2, 1), (3, 1), (4, 1), (5, 1),
    (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
    (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11),
    (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 10), (3, 11),
    (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10), (4, 11),
    (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10), (5, 11),
    (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10), (6, 11),
    (7, 4), (7, 5), (7, 6), (7, 7),
    (8, 4), (8, 5), (8, 6),
    (9, 4), (9, 5);

INSERT INTO course_teacher VALUES
    (1, 1), (2, 1), (3, 1), (4, 1), (11, 1), (12, 1), (5, 1),
    (5, 2), (6, 2), (7, 2),
    (8, 3), (9, 3), (10, 3),
    (11, 4), (12, 4), (13, 4);

INSERT INTO assignments VALUES
    (1,'Zaimplementuj 3 dowolne metody sortowania','assignment.1.sort.PNG','Wstep do sortowania',5,NULL,1),
    (2,'Napisz testy jednostkowe','assignment.2.test.PNG','Testy jednostkowe',5,NULL,1),
    (3,'Oblicz pochodne z zalacznika','assignment.3.pochodne.png','Pochodne',1,NULL,1),
    (4,'Napisz program ktory wykorzystuje wspomniany algorytm','assignment.4.quicksort.png','Sortowanie szybkie',5,NULL,1),
    (5,'Napisz program ktory wykorzystuje wspomniany algorytm','assignment.5.binarysearch.PNG','Wyszukiwanie binarne',5,NULL,1),
    (6, 'Oblicz podane calki', 'assignment.6.calki.PNG', 'Calki - wstep', 1, NULL, 1),
    (7, 'Oblicz podane calki', 'assignment.7.calki.PNG', 'Calki - wstep', 1, NULL, 2),
    (8, 'Oblicz podane calki', 'assignment.8.calki.PNG', 'Calki - wstep', 1, NULL, 3),
    (9, 'SQL - wyzwalacze', 'assignment.9.triggers.PNG', 'Mechanizmy bazodanowe', 14, NULL, 1),
    (10, 'SQL - indeksy', 'assignment.10.indexes.PNG', 'Mechanizmy bazodanowe 2', 14, NULL, 1),
    (11, 'Drzewa binarne', 'assignment.11.lista1.pdf', 'Struktury danych 1', 5, NULL, 1),
    (12, 'Drzewa czerwono-czarne', 'assignment.12.lista2.pdf', 'Struktury danych 2', 5, NULL, 1),
    (13, 'B-drzewa', 'assignment.13.lista3.pdf', 'Struktury danych 3', 5, NULL, 1),
    (14, 'Twierdzenie o rekursji uniwersalnej', 'assignment.14.recurence.rar', 'Zlozonosc obliczeniowa', 5, NULL, 1);

INSERT INTO file_history VALUES (1,'800px-MVC-Process.svg.png','Solution','2019-11-23 14:12:12','solution.1.800px-MVC-Process.svg.png',1),(2,'controller.png','Solution','2019-11-23 14:12:18','solution.2.controller.png',2),(3,'dao.png','Solution','2019-11-23 14:12:23','solution.3.dao.png',3),(4,'data.png','Solution','2019-11-23 14:12:29','solution.4.data.png',4),(5,'Java-EE-vs-Spring-1.jpg','Solution','2019-11-23 14:12:34','solution.5.Java-EE-vs-Spring-1.jpg',5),(6,'How-to-set-up-the-Heroku.png','Assignment','2020-01-06 18:46:37','assignment.3.How-to-set-up-the-Heroku.png',3),(7,'1200px-MySQL.svg.png','Assignment','2020-01-06 18:47:05','assignment.4.1200px-MySQL.svg.png',4),(8,'loop.PNG','Assignment','2020-01-06 18:47:29','assignment.5.loop.PNG',5);
