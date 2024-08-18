### GeoRegistry

**GeoRegistry** is an application designed for downloading and storing geographical data. This console application provides an API for managing data about geographical entities. **GeoRegistry** automatically downloads XML files, extracts them, and stores the data in a PostgreSQL database. The application generates two Docker containers: one for the Java application and another for the PostgreSQL server.

## Getting Started

These instructions will help you set up a copy of the project for development and testing on your local machine.

### Prerequisites

You will need Docker to run the project. Docker will handle the installation of Java, Maven, and other dependencies:

```bash
docker -v      # check the Docker version
```

### Installation and Build

1. **Cloning the Repository**
   ```bash
   git clone https://github.com/krivopolnik/georegistry.git
   cd georegistry
   ```

2. **Launching with Docker**
   Docker will handle the building and deployment of the application:
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

### Verifying Application Functionality

Once the application is up and running, you can verify that it is functioning correctly by following these steps:

1. **Test Data Download and Parsing**:
   Execute the following command to initiate the data download and parsing process:
   ```bash
   curl http://localhost:8081/download-and-parse
   ```
   You should see a response indicating that the data has been successfully downloaded and parsed:
   ```
   Data successfully downloaded and parsed!
   ```

2. **Check Database Entries**:
   Connect to the PostgreSQL database to ensure that the data has been properly stored. Use the following command to log in to your PostgreSQL server:
   ```bash
   psql -h localhost -p 5432 -U georegistry_user -d georegistry_db
   ```
   After logging in (you’ll be prompted for the password), you can check the contents of the tables to verify that the entries have been created successfully:
   ```sql
   \dt  # List all tables in the database

   # Check entries in the 'obec' table
   SELECT * FROM obec;

   # Check entries in the 'cast_obce' table
   SELECT * FROM cast_obce;
   ```

   Here is an example of what the output might look like:
   ```
   List of relations
    Schema |   Name    | Type  |      Owner       
   --------+-----------+-------+------------------
    public | obec      | table | georegistry_user
    public | cast_obce | table | georegistry_user

   id |  kod   |  nazev   |      created_date       
   ----+--------+----------+-------------------------
    1  | 573060 | Kopidlno | 2024-08-17 15:38:53.603

   id |  kod  |  nazev   |      created_date       | obec_kod 
   ----+-------+----------+-------------------------+----------
    1  | 69299 | Kopidlno | 2024-08-17 15:38:53.642 |   573060
    2  | 69302 | Ledkov   | 2024-08-17 15:38:53.647 |   573060
    3  | 97373 | Mlýnec   | 2024-08-17 15:38:53.651 |   573060
    4  | 31801 | Drahoraz | 2024-08-17 15:38:53.655 |   573060
    5  | 31828 | Pševes   | 2024-08-17 15:38:53.659 |   573060
   ```

### Routine Maintenance

Periodically, it's a good idea to clean up your Docker environment to ensure that your system is not cluttered with unused Docker volumes, networks, or containers. To do this, you can use the `docker-compose down` command, which stops and removes the containers, default network, and the images used by your application:

```bash
docker-compose down
```

This will help maintain a clean development environment and prevent potential conflicts or resource issues on your machine.
