
default:
    @just --list

start:
    ./mvnw spring-boot:run

sync-deps: 
    ./mvnw dependency:resolve