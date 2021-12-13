# intersect-db
Компиляция:
```
javac \
    -sourcepath ./ \
    -d build/classes \
        ru/zaek/IntersectDB.java \
        ru/zaek/IntersectDBFile.java
```

Создание jar-файла:
```
echo "Main-Class: ru.zaek.IntersectDBFile" > manifest
jar cfm build/IntersectDB.jar manifest -C build/classes .
java -jar build/IntersectDB.jar
```

Запуск приложения:
```
java -jar IntersectDB.jar
```