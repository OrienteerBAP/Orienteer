## Orienteer module for Apache Camel and OrientDB Apache Camel component

### Возможности
- Создание любого количества конфигураций и их ручной запуск, привязанный к приложению
- Возможность извлечения данных из бд в виде списка/Map/JSON/обьекта
- Возможность записи данных в бд в виде списка/Map/JSON/обьекта
- Подержка всех компонентов кэмэл без их предварительного конфигурирования(только инлайн XML конфигурация)

Для полноценного использования, крайне рекомендуется ознакомится с базовой документацией по Apache Camel

### Параметры
#### URI
В данном компоненте, ***uri*** представляет из себя обычный ***orientdb sql*** запрос, за одним исключением - 
вместо ***?*** для задания анонимных входных параметров используется сочетание знаков ***:#***.

ВАЖНО!!!
Вместо ***?*** для задания анонимных входных параметров используется сочетание знаков ***:#***!
Например ```select from test where name like :#``` вместо ```select from test where name like ?``` 

SDT - Support data types 

#### Входные
|Название 		|	default	|	SDT |   		Назначение 												
|---|---|---|---|
|inputAsOClass	|	null	|	not list	|переназначение корневого класса.
|preload 		|	false	|	not list	|предзагрузить(сохранить) входные данные как обьект, ДО выполнения скуэль запроса или вместо него.Работает только если входные данные можно преобразовать в обьект.
|makeNew 		|	true	|	not list	|воздать новый обьект из входных данных на этапе "preload", если этот этап выполняется

#### Выходные
|Название		|	default		|SDT    	|	Назначение												
|---|---|---|---|
|outputType		|	map		|			|	Тип выходных данных для одной записи БД.
|fetchPlan		|	null	|	json	|	фетчплан.Подробности в документации по orientdb
|maxDepth		|	0		|	map		|	максимальная глубина выборки.
|fetchAllEmbedded	|true	|	map		|	выбрать все встроенные(EMBEDDED) обьекты,независимо от глубины выборки.

***outputType*** может принимать следующие значения

|Название| Описание|
|---|---| 
|map 	| Map Java object (List\<Map\> \- точный формат выходных данных)|
|object 	| ODocument(List\<ODocument\> \- точный формат выходных данных)|
|list	| List(List\<List\> \- точный формат выходных данных)|
|json	| JSON(String \- точный формат выходных данных)|



### Входные данные 

#### Одиночный контекст

|Тип данных|Обработка
|---|---|  
|Map 		| производится попытка преобразования в ODocument.Получивышиеся данные проходят этап preload и передаются в sql запрос  
|ODocument 	| данные проходят этап preload и передаются в sql запрос  

#### Списковый контекст

|Тип данных|Этапы обработки
|---|---|  
|List<Object>	| производится преобразование каждого обьекта в одиночный контекст с последующим вызовом sql запроса(для каждого обьекта)
|List<List>		| входные данные для списка полей воспринимаются только в таком виде	

#### Важное замечание

ВНИМАНИЕ!!!
Если вы передали входные даные в списковом контексте, то и выходные тоже будут в списковом,
причем если в ходе выполнения sql запроса тоже получились данные в списковом контексте, они все будут добавлены в один список
Пример:  
```
<from uri="orientdb://select from test limit 2?outputType=object"/>
<to uri="orientdb://select from test?preload=true&amp;makeNew=true&amp;outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbCopyPast.txt"/>
```
Здесь мы хотим вытащить два элемента класса test, создать их копии и записать в файл все обьекты класса test
Однако в итоге, в файл запишется ```список_обьектов_класса_test + первый_элемент + список_обьектов_класса_test + первый_элемент + второй_элемент```   

Чтобы добится желаемого результата, требуется любым способом обнулить body или другим способом избавится от спискового контекста, например таким образом:
```
<from uri="orientdb://select from test limit 2?outputType=object"/>
<to uri="orientdb://?preload=true&amp;makeNew=true"/>
<from uri="orientdb://select from test?outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbCopyPast.txt"/>
```
### Выходные данные

В случае наличия sql запроса - производится компановка и вывод его результата.
В случае отсутствия - на выход передаются входные данные.
Если на этапе "preload" были созданы обьекты - то они передаются уже с новыми RID

### Примеры

Для редактирования или создания конфигурации вам понадобится доступ к DB классу OIntegrationConfig
Для примера,мы будем использовать следующую структуру:
```
CREATE CLASS testemb
CREATE PROPERTY testemb.key STRING
CREATE PROPERTY testemb.value STRING

CREATE CLASS test
CREATE PROPERTY test.name STRING
CREATE PROPERTY test.tlink LINK test
CREATE PROPERTY test.emb EMBEDDED testemb
```
#### Извлечение из бд и запись в файл
##### Как Map
Наиболее универсальный метод извлечения
Map и List понимает большинство компонентов камел
подходит если вы хотите передать данные дальше, для дальнейшей обработки 

``` 	
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="orientdb://select from test?outputType=map&amp;fetchAllEmbedded=true&amp;maxDepth=0"/>
<to uri="dataformat:json:marshal?prettyPrint=false"/>
<to uri="file://test/?fileName=dbMap.txt"/>
</route>
</routes>
``` 
##### Как JSON
Подходит если вы хотите просто сохранить существующее состояние с возможностью восстановить его позднее

```
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="orientdb://select from test?outputType=json&amp;fetchPlan=*:1"/>
<to uri="file://test/?fileName=dbJson.txt"/>
</route>
</routes>
```

##### Как список
Подходит, если вам надо перевеcти данные в "плоский" формат, например для передачи в эксель или сохранения в виде CSV
Поддерживаются только простые типы данных и ссылки
***ВНИМАНИЕ!!!*** Если вы сохраните ДРЕВОВИДНУЮ структуру таким образом - восстановить вы ее сможете только с помощью другого типа записи(map/json)

```
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="orientdb://select name,tlink,emb.key,emb.value from test?outputType=list"/>
<to uri="dataformat:csv:marshal"/>
<to uri="file://test/?fileName=dbList.txt"/>
</route>
</routes>
```

#### Извлечение из файла и запись в БД

Заметка - в примерах ниже, выходные данные параметризуются и сохраняются в файл только для наглядности

##### Как Map

Подходит если вам требуется сохранить в БД заранее подготовленную структуру данных,
 после какой- то их внешней обработки

```
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="file://test/?fileName=dbMap.txt&amp;noop=true"/>
<to uri="dataformat:json:unmarshal"/>
<to uri="orientdb://?preload=true&amp;makeNew=false&amp;outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbMapR.txt"/>
</route>
</routes>
```

##### Как Map,выборочно
Таким способом нельзя записать встроенные обьекты ODоcument
Ссылки поддерживаются


```
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="file://test/?fileName=dbMap.txt&amp;noop=true"/>
<to uri="dataformat:json:unmarshal"/>
<to uri="orientdb://insert into test set name=:name,tlink=:tlink?preload=false&amp;makeNew=false&amp;outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbMapR.txt"/>
</route>
</routes>
```

##### Как JSON
Простая десеарелизация из JSON

```
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="file://test/?fileName=dbJson.txt&amp;noop=true"/>
<convertBodyTo type="java.lang.String"/>
<to uri="orientdb://?preload=true&amp;makeNew=false&amp;outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbJsonR.txt"/>
</route>
</routes>
```

##### Как список
Таким образом можно сохранить только плоские структуры.
Для сохранения иерархических структур, предварительно понадобится преобразование в соответствующий формат и сохранение в этом формате 

```
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="file://test/?fileName=dbList.txt&amp;noop=true"/>
<to uri="dataformat:csv:unmarshal"/>
<to uri="orientdb://insert into test(name,еlink) values (:#,:#)?outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbListR.txt"/>
</route>
</routes>
```
 
#### Извлечение из бд и запись в БД
##### Как обьект 
Подходит для обработки данных с помощью камэл компонентов ,которые умеют работать с ODocument
В примере - просто копирование первого попавшегося обьекта три раза

```	
<?xml version="1.0" encoding="UTF-8"?>
<routes xmlns="http://camel.apache.org/schema/spring">
<route id="otientdbtest">
<from uri="orientdb://select from test limit 1?outputType=object"/>
<to uri="orientdb://?preload=true&amp;makeNew=true&amp;outputType=object"/>
<to uri="orientdb://?preload=true&amp;makeNew=true&amp;outputType=object"/>
<to uri="orientdb://?preload=true&amp;makeNew=true&amp;outputType=map"/>
<to uri="dataformat:json:marshal?prettyPrint=true"/>
<to uri="file://test/?fileName=dbCopyPast.txt"/>
</route>
</routes>
``` 