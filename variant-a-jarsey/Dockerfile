# Utiliser une base Java 17, comme requis pour le benchmark
FROM eclipse-temurin:17-jdk-focal

# Définir l'emplacement de l'application
WORKDIR /app

# 1. Télécharger l'agent JMX Exporter à l'intérieur du conteneur
ENV JMX_EXPORTER_VERSION 0.20.0
# Télécharge l'agent et le place dans le répertoire de travail (/app)
RUN wget -q https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/$JMX_EXPORTER_VERSION/jmx_prometheus_javaagent-$JMX_EXPORTER_VERSION.jar -O /app/jmx_prometheus_javaagent.jar

# 2. Copier le JAR de l'application et la configuration JMX
# Assurez-vous d'avoir compilé votre projet pour que le JAR soit dans 'target/'
COPY ./target/variant-a-jersey-1.0-SNAPSHOT.jar /app/variant-a-jersey.jar
COPY config.yml /app/config-jersey.yml

# 3. Exposer les ports: 8081 pour le métier, 9401 pour les métriques
EXPOSE 8081 9401

# 4. Définir la commande de démarrage (CMD)
# Tous les fichiers sont maintenant à la racine du conteneur (/app), le chemin est donc simple.
CMD ["java", \
     "-javaagent:./jmx_prometheus_javaagent.jar=**9401**:./config-jersey.yml", \
     "-jar", "variant-a-jersey.jar"]