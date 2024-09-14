## Content

1. [Features](#features)
2. [Installation](#installation)
3. [FAQ](#faq)

## Features

El siguiente proyecto ha sido levantado en un entorno local con las siguientes configuraciones.

```
- OS name: "mac os x", version: "13.1", arch: "x86_64", family: "mac"

```

### Backend

* IDE: IntelliJ IDEA [v: 2022.1.1](https://www.jetbrains.com/idea/download/)
* Framework: [Spring Boot 2.7.15](https://start.spring.io/)
* Gestor y constructor de proyectos: [Maven](https://maven.apache.org/)

```mvn
$ mvn -v

- Apache Maven 3.8.4
- Java version: 17.0.1
- Default locale: es_419, platform encoding: US-ASCII

```

### Frontend

* Framework: [Angular 13.1.1](https://angular.io/docs)
* Template: [Ultima PrimeNG from Primefaces](https://www.primefaces.org/ultima-ng/#/)
* Gestor de paquetes. [NPM](https://www.npmjs.com/)

```node
$ nvm -v && node -v && npm -v

- Node Version Manager: 0.38.0
- Node: v16.17.0
- Package Manager: npm 8.15.0

$ ng --version # después de instalar el proyecto

- Angular CLI: 13.2.6

```

## Installation

### Backend
***
A continuación algunas herramientas a considerar antes de levantar el proyecto

- [Lombok.jar](https://openwebinars.net/blog/que-es-lombok/)
  - [Para versiones anteriores del IDE](https://projectlombok.org/setup/intellij)
  - [Setting up Lombok with Eclipse and Intellij](https://www.baeldung.com/lombok-ide)
- REDIS
  - El proyecto utiliza la base de datos en memoria [Redis](https://redis.io/docs/getting-started/). 
  - Se debe configurar en modo seguro.

#### Maven Profile & Spring Boot Properties
***

A continuación se muestra como desplegar la aplicación en un entorno definido a través de perfiles de configuración. Se definen 3 perfiles que hacen referencia al ambiente local `local`, desarrollo `dev` y producción `prod` pudiendo existir otros más dependiendo de la necesidad.

Copiar el archivo denominado `application-example.properties` que se encuentra en el directorio `src/main/resources` y renombrarlo como el environment seleccionado a levantar, ejemplo `application-local.properties`. Modificar los valores por los del ambiente local.

Crear un archivo en el mismo directorio denominado `application.properties` cuyo contenido es el siguiente.

```properties

spring.profiles.active=@activatedProperties@


```

#### Maven compile & run
***

El archivo `pom.xml` está preparado para desplegar la aplicación dependiendo de la configuración seleccionada a través del parámetro `-P <profile-id>`.

```mvn

# mvn clean package -P <profile-id>
# mvn spring-boot:run -P <profile-id>
# Example:

mvn spring-boot:run -P local

```

En el log debe mostrar un mensaje de nivel `INFO` donde se indica el perfil seleccionado para levantar el entorno, similar a:

```log

2023-09-13 15:34:48.773  INFO 11274 --- [  restartedMain] p.g.m.root.ApiBackendApplication  : The following 1 profile is active: "local"

```

> **Nota:** Los archivos de `application.properties` y sus derivados (`application-local.properties`, `application-dev.properties`, `application-prod.properties`, otros) no deben estar versionados y no serán reconocidos para ser versionados (`.gitignore`).


### Frontend
***

#### Instalar nvm (Node Version Manager)
***

`nvm` permite gestionar múltiples versiones de `Node.js` y `npm` de manera sencilla.

```sh

$ curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.38.0/install.sh | bash
# Este comando descargará e instalará nvm en tu sistema.

```

Cierra y abre la terminal.


**Instalar Node.js y npm con nvm**

```sh

$ nvm install 16.17.0

```

Esto instalará `Node.js` en la versión 16.17.0 y también configurará la versión adecuada de `npm` para esa versión de `Node.js`.

**Configurar proxy local**

Abrir el archivo `/frontend/proxy.config.example.json` y renombrarlo a `proxy.config.json`. Modificar los valores del target para acceso al backend.

```json
{
    "/api/*": {
        "target": "http://{IP or LOCALHOST}:{PORTH}/",
        "secure": false,
        "loglevel": "debug",
        "changeOrigin": true
    }
}
```

**Instalación de `CLI `de angular**

```sh

$ cd /frontend
$ npm install -g @angular/cli

```

Comando `sass` para compilar `css`.

```sh
$ npm install -g node-sass
$ node-sass src/assets/layout/css/layout-light.scss src/assets/layout/css/layout-light.css
$ node-sass src/assets/theme/indigo/theme-light.scss src/assets/theme/indigo/theme-light.css
```

Instalar paquetes y librerias del `package.json`

```sh
$ npm install
```

Levantar instancia de desarrollo

```sh
$ npm start
```

Compilar el proyecto frontend para ambiente de producción. `--output-hashing=all` borra/renueva el caché del proyecto

```sh

$ npm run build --output-hashing=all

```

## FAQ
***

* [Que es lombok?](https://openwebinars.net/blog/que-es-lombok/)
* [Instalar Lombok en eclipse](https://programmerclick.com/article/95721584982/)
* [Support for Ivy #81 | ngx-store ](https://github.com/zoomsphere/ngx-store/issues/81#issuecomment-607012983)
* [Maven Profile & Spring Boot Properties](https://medium.com/@derrya/maven-profile-spring-boot-properties-a34f2b2bb386)
* [NPM node-sass](https://www.npmjs.com/package/node-sass)
* [Jaspersoft® Studio 6.20.5 - Visual Designer for JasperReports 6.20.5](https://community.jaspersoft.com/project/jaspersoft-studio)
* [Redis](https://redis.io/docs/getting-started/)