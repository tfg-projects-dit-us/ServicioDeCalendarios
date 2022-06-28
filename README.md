# ServicioDeCalendarios

Este repositorio es una ampliación de un servicio REST  [*Guardians Service*:](https://github.com/tfg-projects-dit-us/guardiansRESTinterface) 

Este servicio permite la genración de turnos y la gestion de un servicio de calendarios. 

## Desarrollo
To work on this project, there are mainly four steps to be taken:
1. Install Java 1.8
2. Install Lombok and enable its anotation preprocessing in the desired IDE
3. Configure the database
4. Configure the IDE
5. Configure the integration with the scheduler

Further instructions on the first four steps can be found [here](https://github.com/miggoncan/guardiansRESTinterfaceDoc/blob/master/setup/setup.md).

### Configure integration with the scheduler
1. Follow the setup instructions for the scheduler. Found [here](https://github.com/miggoncan/guardiansScheduler#setup-instructions).
2. Configure the `resource/application.properties`:
    1. Change the property `scheduler.command` to be the path to a 
       python interpreter, version 3.7+.

       E.g. `scheduler.command = python3.7` (supossing the binary is in the PATH)
    2. Change the property `scheduler.entryPoint` to the path to the 
       `src/main.py` file of the scheduler. For example, if the scheduler 
       repository was in `/home/guardians/Documents/scheduler`, the property 
       should have the value `/home/guardians/Documents/scheduler/src/main.py`:

       `scheduler.entryPoint = /home/guardians/Documents/scheduler/src/main.py`
    3. The properties `scheduler.file.*` will be the temporary files 
       used for communication between the REST service and the scheduler.
       Make sure both of them have read and write privileges on these files.
       For example:

       ```
       scheduler.file.doctors = /tmp/doctors.json
       scheduler.file.shiftConfs = /tmp/shiftConfs.json
       scheduler.file.calendar = /tmp/calendar.json
       scheduler.file.schedule = /tmp/schedule.json
       ```
    4. [Optional] The property `scheduler.timeout` is an integer that 
       will indicate the number of minutes to wait before killing the 
       scheduler process and considering the schedule generation failed 
       (after a request to generate a schedule).

## Producción
1. Clona este repositorio
2. Modifica los siguientes parámetros del archivo `resource/application.properties`   
   1. `spring.datasource.url=jdbc:mysql://myapp-mysql:3306/db_guardians?serverTimezone=UTC`
   2. `spring.jpa.hibernate.ddl-auto=validate`
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
