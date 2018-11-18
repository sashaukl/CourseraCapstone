# CourseraCapstone
Made by Alexandr Ukolov
Very simple WeatherApp. Support only 5 cities. 
Contains 2 activities. Main activity with temperature and city name and second with settings where you can change city. 
Only 5 cities support.
When main activity starts it also starts a servise where app get data from web. To get data i used free subscribe of https://openweathermap.org/.
Servise writes all data to mySQL via ContentProvider and then in loop main activity wtites it to fields in xml files.
To interact with different apps i used explicit and implicit intents. Explicit to change activities and implicit with broadcast reciver to change any data.
picture with data is Schema.bmp
