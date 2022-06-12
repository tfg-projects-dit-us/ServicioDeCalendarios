FROM carcohcal/enviorement:test
RUN git clone https://github.com/tfg-projects-dit-us/guardiansScheduler.git
WORKDIR /guardiansScheduler
RUN python3.7 -m pip install -r requirements.txt
WORKDIR /guardiansScheduler/src
RUN chmod 777 main.py && chmod 777 scheduler.py
WORKDIR ../..
EXPOSE 8080
COPY build/libs/guardians-0.0.1.jar app.jar
RUN chmod 777 app.jar
ENTRYPOINT ["java","-jar","/app.jar"]