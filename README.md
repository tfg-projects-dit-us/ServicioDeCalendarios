# ServicioDeCalendarios

Este repositorio es una ampliación de un servicio REST  [*Guardians Service*:](https://github.com/tfg-projects-dit-us/guardiansRESTinterface) 

Este servicio permite la generación de turnos y la gestión de un servicio de calendarios. 

## Desarrollo
Para trabajar en este proyecto, hay principalmente cuatro pasos a seguir:
1. Instalar Java 1.8
2. Instalar Lombok y habilitar su preprocesamiento de anotación en el IDE deseado
3. Configurar la base de datos
4. Configurar el IDE
5. Configurar la integración con el scheduler

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
2. Clona este repositorio y elimina el archivo /src/main/java/guardians/LoadInitialData.java. Este fichero se utiliza en producción, para reiniciar la base de datos en cada ejecución con unos valores iniciales.
3. Modifica los siguientes parámetros del archivo `resource/application.properties`   
   1. Aquí se modifica localhost por el nombre del contenedor de la base de datos para que Docker lo relacione `spring.datasource.url=jdbc:mysql://myapp-mysql:3306/db_guardians?serverTimezone=UTC`
   2. Se modifica este parámetro para que la base de datos sea persistente y no se reinicie en cada ejecución.  
   `spring.jpa.hibernate.ddl-auto=validate`
   3. Los siguientes parámetros con los correspondientes al servicio de calendario que se utilice como backend para la gestión de calendarios.
    ```calendario.user = USUARIO DEL CLAENDARIO
       calendario.psw =  CONTRASEÑA DEL CALENDARIO
        calendario.uri = URI CALDAV DEL CALENDARIO
     ```
   4. Los siguientes parámetros con los correspondientes al servicio de email que se utilice como backend para el envío de correos electrónicos.
    ```
    email.host =  HOST DEL SERVICIO DE EMAIL
    email.loggin = USUARIO DEL EMAIL
    email.password = CONTRASEÑA EMAIL
    ```
3. Ejecuta la tarea `gradle bootjar` para crear el .jar del servicio
4. Crea la imagen de  Docker ejecutando el comando `docker build -t yourusername/repository-name:tag . `
5. Modifica el archivo docker-compose con el nombre de la imagen que acabas de crear

  ```
  myapp-main:
     image: yourusername/repository-name:tag 
  ```
        
6. Ejecuta `docker-compose -f /path/docker-compose.yml up -d `

A partir de este instante tendrá disponible en ----- el servicio de gestión de turnos y calendarios
