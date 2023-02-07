# ServicioDeCalendarios

Este repositorio es una ampliación de un servicio REST  [*Guardians Service*:](https://github.com/tfg-projects-dit-us/guardiansRESTinterface) 

Este servicio permite la generación de turnos y la gestión de un servicio de calendarios. 

## Desarrollo
Para trabajar en este proyecto, hay principalmente cuatro pasos a seguir:
1. Instalar Java 1.8
2. Instale Lombok y habilite su preprocesamiento de anotación en el IDE deseado
3. Configurar la base de datos
4. Configurar el IDE
5.Configurar la integración con el scheduler

Se pueden encontrar más instrucciones sobre los primeros cuatro pasos [aquí](https://github.com/miggoncan/guardiansRESTinterfaceDoc/blob/master/setup/setup.md).

### Configurar la integración con el scheduler
1. Siga las instrucciones de configuración del programador. Encontrado [aquí](https://github.com/miggoncan/guardiansScheduler#setup-instructions).
2. Configure el 'resource/application.properties':
    1. Cambie la propiedad 'scheduler.command' para que sea la ruta a un 
       intérprete de python, versión 3.7+.


       Por ejemplo, 'scheduler.command = python3.7' (suponiendo que el binario está en el PATH)
    2. Cambie la propiedad 'scheduler.entryPoint' a la ruta a la 
       Archivo 'src/main.py' del programador. Por ejemplo, si el programador 
       el repositorio estaba en '/home/guardians/Documents/scheduler', la propiedad 
       debe tener el valor '/home/guardians/Documents/scheduler/src/main.py':
       
       `scheduler.entryPoint = /home/guardians/Documents/scheduler/src/main.py`
    3. Las propiedades 'scheduler.file.*' serán los archivos temporales 
       se utiliza para la comunicación entre el servicio REST y el programador.
       Asegúrese de que ambos tengan privilegios de lectura y escritura en estos archivos.
       Por ejemplo:

       ```
       scheduler.file.doctors = /tmp/doctors.json
       scheduler.file.shiftConfs = /tmp/shiftConfs.json
       scheduler.file.calendar = /tmp/calendar.json
       scheduler.file.schedule = /tmp/schedule.json
       ```
    4. [Opcional] La propiedad 'scheduler.timeout' es un entero que 
       indicará el número de minutos que debe esperar antes de matar el 
       Proceso del programador y teniendo en cuenta que se ha producido un error en la generación de la programación 
       (después de una solicitud para generar un cronograma).


## Producción
1. Instala [Docker](https://docs.docker.com/engine/install/ubuntu/)
2. Clona este repositorio, los archivos están en la rama despliegue.
3. Crea un token de autentificación de github con permisos para leer paquetes [Guía](https://docs.github.com/es/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token).
1. En el archivo docker-compose-calendario.yml se define el servicio de calendario.
Configurar la base de datos que persistirá los calendarios generados, creando la contraseña de administrador (POSTGRES_PASSWORD), la contraseña de la base de datos *(PGSQL_ROOT_PASS)*, configurada anteriormente *(POSTGRES_PASSWORD)*, y las contraseñas de usuario *(PASSDAVDB)* y administrador *(ADMINDAVICALPASS)* del servidor además del hostname.
1. Iniciar el servicio con el siguiente comando: `Docker compose -f /path/docker-compose-calendario.yml up -d`
1. Conectarse a http://localhost e iniciar sesión con la cuenta de administrador para crear al menos un usuario con permisos de escritura y lectura aparte del administrador. 
3. Modifica los siguientes parámetros del archivo `resource/application.properties`   
   1. Los siguientes parámetros con los correspondientes a tu servicio de calendario creado en el paso anterior.
        ```calendario.user = USUARIO DEL CLAENDARIO
           calendario.psw =  CONTRASEÑA DEL CALENDARIO
            calendario.uri = http://calendar/caldav.php/<Usuario>/calendar/<Nombre del calendario>
         ```
   4. Los parámetros correspondientes al servicio de email a utilizar.
        ```email.host =  HOST DEL SERVICIO DE EMAIL
        email.loggin = USUARIO DEL EMAIL
        email.password = CONTRASEÑA EMAIL
        ```
    1. Parámetros de la base de datos myql.
        ```
        spring.datasource.username= USUARIO DE LA BASE DE DATOS
        spring.datasource.password= CONTRASEÑA DE LA BASE DE DATOS
        ```

3. En el fichero Dockerfile deberá sustituir *Authorization token* con el token generado en el paso 3.
4. Crea la imagen de  Docker `docker build -t yourusername/repository-name:tag . `
5. En el fichero docker-compose-rest.yml se definen las variables de configuración del servicio REST y la base de datos mysql. 

    1. En myapp-mysql configurar el nombre de base datos *(MYSQL_DATABASE)*, el usuario y la contraseña *(MYSQL_USER & MYSQL_PASSWORD respectivamente)*. Estos deben coincidir con los del archivo *application.properties*. Las tablas populate_sql se deberán modificar con los datos correspondientes para cada caso de uso. 
    1. myapp-main configurar los parámetros de la base de datos acorde con los modificados en el punto anterior. 
    2. Modifica el nombre de la imagen acorde a la creada en el paso 9.
          ```
          myapp-main:
             image: yourusername/repository-name:tag 
          ```
        
6. Ejecuta `docker-compose -f /path/docker-compose-rest.yml up -d `
