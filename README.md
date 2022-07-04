# ServicioDeCalendarios

Este repositorio es una ampliación de un servicio REST  [*Guardians Service*:](https://github.com/tfg-projects-dit-us/guardiansRESTinterface) 

Este servicio permite la genración de turnos y la gestion de un servicio de calendarios. 

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
2. Clona este repositorio y elimina el archivo /src/main/java/guardians/LoadInitialData.java
3. Modifica los siguientes parámetros del archivo `resource/application.properties`   
   1. Aquí se modifica localhost por el nombre del contedor de la base de datos para que Docker lo realicione `spring.datasource.url=jdbc:mysql://myapp-mysql:3306/db_guardians?serverTimezone=UTC`
   2. Se modifica este parámetro para que la base de datos sea persistente y no se reinicie cada ejecución.  
   `spring.jpa.hibernate.ddl-auto=validate`
   3. Los siguientes parámetros con los correspondientes a tu servicio de calendario
    ```calendario.user = USUARIO DEL CLAENDARIO
       calendario.psw =  CONTRASEÑA DEL CALENDARIO
        calendario.uri = URI CALDAV DEL CALENDARIO
     ```
   4. Los siguientes parámetros con los correspondientes a tu servicio de email
    ```
    email.host =  HOST DEL SERVICIO DE EMAIL
    email.loggin = USUARIO DEL EMAIL
    email.password = CONTRASEÑA EMAIL
    ```
3. Ejecuta la tarea `gradle bootjar`
4. Crea la imagen de  Docker `docker build -t yourusername/repository-name:tag . `
5. Modifica el archivo docker-compose con el nombre de la imagen que acabas de crear

  ```
  myapp-main:
     image: yourusername/repository-name:tag 
  ```
        
6. Ejecuta `docker-compose -f /path/docker-compose.yml up -d `
