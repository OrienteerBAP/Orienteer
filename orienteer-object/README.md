### Orienteer object

Allows use library [guice-persist-orient](https://github.com/xvik/guice-persist-orient) in Orienteer 
(Features like repositories, schema object mapping and other).

#### Steps for use this module
1. Include module into classpath
2. If need "Object scheme mapping" feature need specify Java packages where exists
    classes annotated with `@Persistent`. Uses Guice Multibindings:
    ```java
        Multibinder<String> binder = Multibinder.newSetBinder(binder(), String.class, Names.named("orient.model.packages"));
        binder.addBinding().toInstance("path.to.model");
    ```
    See:
    * [Guice Multibindings](https://github.com/google/guice/wiki/Multibindings) in Google Guice
    * [Schema initialization](https://github.com/xvik/guice-persist-orient#scheme-initialization) in guice-persist-orient