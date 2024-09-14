# DESPLIEGUE BACKEND

## Paso 1 - Instalar OpenJDK11

Verificar versión de Java

```sh
$ java -version
```

Instalar la siguiente versión

```sh
$ sudo yum install java-11-openjdk-devel
$ sudo yum install java-11-openjdk

```

## Paso 2 - Generar el ejecutable

### Configuración inicial
***

La siguiente configuración hace referencia al ambiente de producción, con otros ambientes se debe seguir los mismos pasos cambiando la nomenclatura de los environments.

Copiar el archivo denominado `application-example.properties` que se encuentra en el directorio `src/main/resources` y renombrarlo como `application-prod.properties`. Modificar los valores por los del ambiente de producción.

Crear un archivo en el mismo directorio denominado `application.properties` cuyo contenido es el siguiente.

```properties

spring.profiles.active=@activatedProperties@


```

> **Nota:** Los archivos de `application.properties` y sus derivados (`application-local.properties`, `application-dev.properties`, `application-prod.properties`) no están versionados y no serán reconocidos para ser versionados.


#### Generar .jar
***

Para crear un `jar` 'totalmente ejecutable' con Maven, verifique si el archivo de configuración `pom.xml` contiene el siguiente fragmento de código:

```mvn

<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <executable>true</executable>
    </configuration>
</plugin>

```
> **Nota:** Si no se encuentra el fragmento de código puede utilizar la configuración de complemento descrito arriba.

Generar el ejecutable en `src/main/resources`

```sh

$ mvn clean package -P prod

```

En el log debe mostrar un mensaje de nivel `INFO` donde se indica el perfil seleccionado para levantar el entorno, similar a:

```log

2023-09-13 15:34:48.773  INFO 11274 --- [  restartedMain] p.g.m.root.ApiBackendApplication  : The following 1 profile is active: "prod"

```

Copiar el archivo `.jar` generado `[nombre-aplicacion].jar` al servidor. Se puede levantar la aplicación ejecutando el comando `java -jar [nombre-aplicacion].jar` pero se recomienda levantar como servicio del sistema operativo.

## Paso 3 - Desplegar el ejecutable como servicio

Se asume que la aplicación fue copiada en el directorio `/opt/[directorio-aplicacion]/`. Para instalar una aplicación Spring Boot como un servicio, se debe crear un enlace simbólico de la siguiente manera:

```sh
$ sudo ln -s /opt/[directorio-aplicacion]/[nombre-aplicacion].jar /etc/init.d/[enlace-aplicacion]

# Ejemplo: sudo ln -s /opt/adminpy/backend-adminpy.jar /etc/init.d/adminpy
```

Una vez instalado, se puede iniciar y detener el servicio de la forma habitual. Por ejemplo, en un sistema basado en Debian, podría iniciarlo con el siguiente comando:

```sh

$ service [enlace-aplicacion] start

# start, stop, restart, status

```

## FAQ
***

* [Instalar OpenJDK11 para CentOS 7.9](https://phoenixnap.com/kb/install-java-on-centos)
* [Manual de instalación Spring | 2.1. Supported Operating Systems](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.installing)
* [Maven Profile & Spring Boot Properties](https://medium.com/@derrya/maven-profile-spring-boot-properties-a34f2b2bb386)
