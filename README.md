# Мой самописный сервер

Я создал этот сервер, изучая взаимодействие Java и Web. Я начал писать сервер при выполнении домашнего задания на
курсах и расширил его для лучшего понимания процессов сетевого взаимодействия.

Это учебная, а не промышленная разработка и не попытка её имитировать.

**Сейчас сервер:**

* Обрабатывает GET и POST запросы, парсит их содержимое.
* Использует кастомные Handler'ы для различных запросов.
* Обрабатывает параметры, переданные в http-запросе.
* Обрабатывает данные из форм в формате x-www-form-urlencoded.

**В работе:** 
* Чтение файлов из запросов multipart/form-data.

*****

# My custom WEB-server
I created this server while studying Java and Web interaction. 
I started writing the server while doing homework on Java web-courses and extended trying to understand
networking processes by myself.

This is educational, not industrial product and not an attempt to imitate it.

**The Server does:**

* Processes GET and POST requests, parses their content.
* Uses custom Handlers to handle various http-requests.
* Processes the parameters passed through the http-request.
* Processes data sent via forms in x-www-form-urlencoded format.

**Working on:**

* Reading files from multipart/form-data requests.

