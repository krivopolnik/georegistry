### GeoRegistry

**GeoRegistry** is an application designed for downloading and storing geographical data. This console application provides an API for managing data about geographical entities. **GeoRegistry** automatically downloads XML files, extracts them, and stores the data in a PostgreSQL database. The application generates two Docker containers: one for the Java application and another for the PostgreSQL server.

## Getting Started

These instructions will help you set up a copy of the project for development and testing on your local machine.

### Prerequisites

You will need the following tools to run the project:

- **Java 21**: Ensure you have the required version of Java installed.
- **Docker**: Necessary for managing the application and database containers.
- **Maven**: Required for building the project.

```bash
java -version  # check the installed version of Java
docker -v      # check the Docker version
mvn -version   # check the Maven version
```

### Installation and Build

1. **Cloning the Repository**
   ```bash
   git clone https://github.com/krivopolnik/georegistry.git
   cd georegistry
   ```

2. **Building the Project**
   Before running Docker, you need to build the `.jar` file of the project:
   ```bash
   mvn clean install -DskipTests
   ```

3. **Launching with Docker**
   ```bash
   cd src/docker
   docker-compose up --build
   ```

### Configuration

Before launching, ensure that the configuration files in `src/main/resources` are correctly set up for your environment. This may include database settings, security parameters, and other environment variables.

## Usage
After launching, the application will be accessible at `http://localhost:8081/`. To activate the main functionality, use the following curl command:

```bash
curl http://localhost:8081/download-and-parse
```
