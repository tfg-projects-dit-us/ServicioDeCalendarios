FROM ubuntu:18.04
RUN apt-get update && apt-get install openjdk-8-jdk -y
RUN apt install software-properties-common -y && add-apt-repository ppa:deadsnakes/ppa -y
RUN apt install python3.7 -y
RUN apt-get -y install python3-pip
RUN apt-get install git -y
RUN git clone https://github.com/tfg-projects-dit-us/guardiansScheduler.git
WORKDIR /guardiansScheduler
RUN python3.7 -m pip install -r requirements.txt
WORKDIR /guardiansScheduler/src
RUN chmod 777 main.py && chmod 777 scheduler.py
WORKDIR ../..
EXPOSE 8080
COPY guardians-0.0.1.jar app.jar
COPY application.properties application.properties
RUN chmod 777 app.jar
ENTRYPOINT ["java","-jar","/app.jar","--spring.config.location=file:///application.properties"]