FROM openjdk:17
ADD build/libs/cicd*.jar cicd*.jar
ENTRYPOINT ["java", "-jar", "cicd*.jar"]
EXPOSE 8787