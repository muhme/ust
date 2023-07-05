# Use the official Tomcat image as a base
FROM tomcat:10.1.10-jdk21-openjdk-slim

# some comfort
RUN apt-get update -qq && apt-get upgrade -y && apt-get install -y net-tools vim

# Compile Java source files
ADD webapps /usr/local/tomcat/webapps
RUN javac -classpath /usr/local/tomcat/lib/servlet-api.jar -d /usr/local/tomcat/webapps/ust/WEB-INF/classes /usr/local/tomcat/webapps/ust/WEB-INF/src/de/hlu/ust/*.java

RUN mkdir /var/ust 

# Expose the Tomcat port
EXPOSE 8080

