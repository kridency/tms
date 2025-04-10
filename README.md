# Task Management System

## Инструкции по локальному запуску

Запуск программы производится посредством перехода в интернет-браузере по ссылке "https://localhost:8443/swagger-ui/swagger-ui/index.html".

Первоначальные настройки отсутствуют.

Программа предусматривает регистрацию пользователя посредством api команды "/api/auth/register" и последующую аутентификацию с помощью команды api "/api/auth/signin.
В результате выполнения команды на экран возвращается authorization token, который необходимо использовать для последующего запуска команд из группы "/api/tasks/*"/.