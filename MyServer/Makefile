# Makefile for Maven-based Java project

.PHONY: build run clean

build:
	@echo "Building the project..."
	mvn clean install

run:
	@echo "Running the application..."
	mvn spring-boot:run

clean:
	@echo "Cleaning the project..."
	mvn clean

all: build run
