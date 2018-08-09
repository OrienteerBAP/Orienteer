### Orienteer users

Allows easy configure multi user systems based on Orienteer.

#### Changes
1. Updates permissions for role "reader"

    | Resource name          | Permissions |
    |------------------------|-------------|
    | class                  | DENY ALL    |
    | class.OPerspectiveItem | READ        |
    | class.OPerspective     | READ        |
    | class.ORole            | READ        | 
    | feature.search         | DENY ALL    |
    | feature.schema         | DENY ALL    |
    
2. Creates new role "orienteerUser" which is inherited from "reader"

    > Contains permissions duplicates from role "reader". Will be removed after fix https://github.com/orientechnologies/orientdb/issues/8338
    
    ##### Unique permissions for role "orienteerUser"
    | Resource name    | Permissions  |
    |------------------|--------------|
    | class.OWidget    | READ         |
    | class.ODashboard | READ         |
    | feature.search   | READ         |
    | class.OUser      | READ, UPDATE |
    | database.cluster | READ, UPDATE |
    
    ##### Duplicates from role "reader"
    | Resource name           | Permissions |
    |-------------------------|-------------|
    | class.OPerspectiveItem  | READ        |
    | class.OPerspective      | READ        |
    | class.ORole             | READ        |
    | schema                  | READ        |
    | cluster.internal        | READ        |
    | record.hook             | READ        |
    | database                | READ        |
    | database.systemclusters | READ        |
    | database.function       | READ        |
    | database.command        | READ        |
    
3. Added new properties to class OUser

    | Property         | Description                                                                            |
    |------------------|----------------------------------------------------------------------------------------|
    | firstName        | User first name                                                                        |
    | lastName         | User last name                                                                         |
    | email            | User email. Indexed as unique. Used as name of user account in default implementation. |
    | id               | User id. Indexed as unique. Uses for register user                                     |
    | restoreId        | User restore id. Uses for restore user password                                        |
    | restoreIdCreated | Timestamp when restore id was created. Need for remove user restore id after some time |

4. Class OIdentity inherited from ORestricted
    > User can read their document, but user can't read other user documents 
5. Created default pages for register user and restore user password
6. For restore password and register new user uses service `IOrienteerUsersService` with default implementation `OrienteerUsersService`