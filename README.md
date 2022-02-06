# Лабораторная работа Тинькофф: просмотрщик сайта Developer Life

Итак, что есть в лабе:

* Просмотр GIF из категорий Последнее, Случайное, Лучшее, Горячее
* Отображение описания, автора, количества голосов
* Переход между GIF по свайпу или по нажатию на кнопки
* Автоматическая подгрузка новых картинок
* Возможность поделиться постом
* Опция для вертикальной прокрутки aka лента TikTok
* Отображение картинки при загрузке и при ошибке

Технические особенности:

* Kotlin, Jetpack Compose, Accompanist (systemuicontroller, pager, pager-indicator)
* Библиотеки Material и Material 3
* Отображение и кеширование GIF через Coil (кеширует через OkHttpClient)
* Обращение к серверу за JSON через OkHttp
* В JSON запрашивается 10 постов (кроме раздела Random)
* Сохранение состояния при смене темы телефона или изменении типа прокрутки

Примечание: сделано не совсем по ТЗ, но, на мой взгляд, изменения улучшают опыт использования

Релизный билд должен быть в разделе Releases

<img src="https://telegra.ph/file/980cbd194e0a9eede7a61.png" alt="Latest" width="200"/>
<img src="https://telegra.ph/file/4dede6746871b37b9e37b.png" alt="Random" width="200"/>
<img src="https://telegra.ph/file/6f562fd24d7ef2cc00b76.png" alt="Top" width="200"/>
<img src="https://telegra.ph/file/4de75a6e179017338133f.png" alt="Dark theme 🌚" width="200"/>

