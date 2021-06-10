# guardiansRESTinterface
This repository implements the REST interface for the *Guardians Service*: A service to schedule and manage medical doctors' shifts.

Currently, it only allows scheduling shifts.

## Understanding the service
The **complete explanation for this project** can be found [here](https://idus.us.es/handle/11441/100991?locale-attribute=en).

The JavaDoc for the REST interface can be found [here](https://miggoncan.github.io/guardiansRESTinterfaceDoc).

The scheduler repository can be found [here](https://github.com/miggoncan/guardiansScheduler).

## Development
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

## Production
To deploy the guardians application for a production environment, visit [this repository](https://github.com/miggoncan/guardiansDeployment).
