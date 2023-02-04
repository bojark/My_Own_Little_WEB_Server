# Мой самописный сервер

Я создал этот сервер, изучая взаимодействие Java и Web. Я начал писать сервер при выполнении домашнего задания на
курсах и расширил его для лучшего понимания процессов сетевого взаимодействия.

Это учебная, а не промышленная разработка и не попытка её имитировать.

**Сейчас сервер:**

* Обрабатывает GET и POST запросы, парсит их содержимое.
* Использует кастомные Handler'ы для различных запросов.
* Обрабатывает параметры, переданные в http-запросе.
* Обрабатывает данные из форм в формате x-www-form-urlencoded.

**Как работать с сервером?**

Функциональность сервера можно проверить через набор демонстрационных страниц. 
В этой версии доступны такие адреса:

* */classic.html* -- загрузка классической htlm-страницы с динамическим контентом (точное время на момент загрузки страницы).
* */resources.html* -- статическая страница с несколькими источниками контента.
* */clicker.html* -- страница с js-скриптом.
* */forms.html* -- форма, отправляет параметры в GET-запросе.
* */post_form_url.html* -- форма, отправляет параметры в POST-запросе в формате application/x-www-form-urlencoded. 
* */teapot.html* -- запрос возвращает ошибку **418 I’m a teapot**.

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

**How to work with the server?**

The functionality of the server can be tested through a set of demo pages.
The following addresses are available in this version:

* */classic.html* -- loading a classic html page with dynamic content (current time).
* */resources.html* -- static page with multiple content sources.
* */clicker.html* -- page with js script.
* */forms.html* -- form, sends parameters in a GET request.
* */post_form_url.html* -- form, sends parameters in a POST request in the "application/x-www-form-urlencoded" format.
* */teapot.html* -- request returns error **418 I'm a teapot**.

**Working on:**

* Reading files from multipart/form-data requests.