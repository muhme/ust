# Dockerfile – ust web application
#
# ust web application, Copyright (c) 2023 Heiko Lübbe, MIT License, https://github.com/muhme/ust

# Use the official Tomcat image as a base
FROM tomcat:11.0-jdk21-openjdk-slim

# net-tools, vim and curl for comfort
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update -qq \
	&& apt-get upgrade -y \
	&& apt-get install -y --no-install-recommends net-tools vim tzdata curl \
	&& rm -rf /var/lib/apt/lists/*

# Compile Java source files
ADD webapps /usr/local/tomcat/webapps
ADD checkstyle.xml /usr/local/tomcat/webapps/ust/
RUN javac -classpath /usr/local/tomcat/lib/servlet-api.jar -d /usr/local/tomcat/webapps/ust/WEB-INF/classes /usr/local/tomcat/webapps/ust/WEB-INF/src/de/hlu/ust/*.java

RUN mkdir /var/ust

# Expose the Tomcat port
EXPOSE 8080

