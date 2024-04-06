# API JMeter Inventory
Application that manages a product inventory fed by product entry or outflow

## ✔️ Requirements
- Java Development Kit 17 (jdk17)

## 🍔 Stack
- Spring boot 3.2.4
- PostgreSQL

## ✈️ How to run locally
1. First add all environment variables or run through the IDE using the `local.env` file
```shell
## Variables you have to create on system or env file
SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5431/apijmeter
SPRING_DATASOURCE_USERNAME=apijmeter
SPRING_DATASOURCE_PASSWORD=
```
2. Start the dependencies
```shell
## run docker compose
docker-compose up
```
3. Execute the application (it will start on port 8080)
```shell
## start web application
./gradlew bootRun
```
