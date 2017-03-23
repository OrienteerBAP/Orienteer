## Orienteer BIRT implementation

### What is this?

This is module for using [BIRT](https://www.eclipse.org/birt/) reports  as [Orienteer](http://orienteer.org/) widget

### How to use?

You have working BIRT report and need to use it in Orienteer?
Follow next steps:

- Load ***orienteer-birt*** module
- Add widget ***Birt Report*** to object or list of objects by your choise. 
- Open widget properties, load your report into field ***Report*** and press ***Save***
- Configure report parameters(if it is need)
- Open your object or list of objects(Or hard refresh opened window by deleting numbers after "?" symbol in address string)
- Now you can see your report.(Or error window, if you do something wrong :))

### Configuration


BIRT widgets parameters

|Name|Type|Description
|---|---|---
|Report|binary|BIRT report file(.rptdesign)
|Parameters|Map<ParameterName,String>|Default value of report parameters for this widget.
|Visible Parameters|Set<ParameterName>|These parameters should be displayed on report page as input fields
|Use Local DB|boolean|***Only for OrientDB(OrientDB ODA BIRT driver)*** Use Orienteer current DB and user settings(uri,username,password) instead report DB settings   