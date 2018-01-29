## Orienteer ETL

### Description

Orienteer ETL it`s OrientDB ETL moduile interface for Orienteer.

First read OrientDB ETL moduile [documentation](http://orientdb.com/docs/2.2.x/ETL-Introduction.html)(For OrientDB v 2.2.x)

Now we can make and run OrientDB ETL scripts from Orienteer.

### How to use

- Make new OETLConfig (Orienteer object with OETLConfig class)
- Set name for your script
- Set ETL config script into ***Config*** field
- Press ***Save*** button
- Press ***Run*** button.
- Now we see ETL script log. For refresh - press ***F5*** button(keyboard button).

### Example

#### Issue

We need to import data from csv file to DB.

#### Solution

##### Example CSV file

```
ID,PARENT_ID,LAST_YEAR_INCOME,DATE_OF_BIRTH,STATE
0,-1,10000,1990-08-11,Arizona
1,0,12234,1976-11-07,Missouri
2,0,21322,1978-01-01,Minnesota
3,0,33333,1960-05-05,Iowa
```

##### Example script for that file

```
{
  "source": { "file": { "path": "D:/temp/source.csv" } },
  "extractor": { "csv": {} },
  "transformers": [
    { "vertex": { "class": "test1" } }
  ],
  "loader": {
    "orientdb": {
       "dbURL":"plocal:Orienteer",
        "classes": [
         {"name": "test1", "extends": "V"}
       ]
    }
  }
}    
```

##### Example script hints

|Variable|Description
|---|---|
|source|Data source.For now we use file data source with. Here ***path*** - absolute path to file(you may try relative path, but it depends by your server configuration)
|extractor|Extractor type (we extract data from csv file)
|transformers|Transformers list (we transform data to vertexes with ***test1*** class)
|loader|Db loader. Here ***dbURL*** - link to local OrientDB,***classes*** - list of classes used for receive data from "transformers"


#### Conclusion

More ETL docs in [OrientDB ETL moduile documentation](http://orientdb.com/docs/2.2.x/ETL-Introduction.html)(For OrientDB v 2.2.x)
 