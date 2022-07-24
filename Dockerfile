FROM andrewem/serviciodecalendarios:base
RUN git clone https://github.com/tfg-projects-dit-us/guardiansScheduler.git
WORKDIR /guardiansScheduler
RUN python3.7 -m pip install -r requirements.txt
WORKDIR /guardiansScheduler/src
RUN chmod 777 main.py && chmod 777 scheduler.py
WORKDIR ../..
EXPOSE 8080
COPY application.properties application.properties
RUN curl 'https://maven.pkg.github.com/tfg-projects-dit-us/ServicioDeCalendarios/guardians.guardians/0.0.2/guardians-0.0.2.jar' \
   -H "Authorization: token <Authorization token>" \
   -L \
     -O
RUN chmod 777 guardians-0.0.2.jar
ENTRYPOINT ["java","-jar","guardians-0.0.2.jar","--spring.config.location=file:///application.properties"]