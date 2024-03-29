FROM openjdk:17
ADD ee-dashboardd-0.0.1-snapshot.jar ee-dashboardd-0.0.1-snapshot.jar
ENTRYPOINT ["java","-Dspring.profiles.active=test","-jar","ee-dashboardd-0.0.1-snapshot.jar"]
EXPOSE 4040