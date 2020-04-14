

`FIN-BANK Trading App`
=====================


A spring-boot RESTful application providing services to handle Futures products transactions done by the business users of a hypothetical financial organisation - `FIN-BANK`


### Requirements
This applications allows the business users to:

    1. See the Total Transaction Amount of each unique product they have done for the day
    2. Upload an Input file (txt) containing daily transactionss
    3. Generate a daily summary report (JSON or CSV)

The CSV report should have the following Headers:


| Header                     | Fields from Input file                                        |
|:---------------------------|:--------------------------------------------------------------| 
| Client_Information         | CLIENT TYPE, CLIENT NUMBER, ACCOUNT NUMBER, SUBACCOUNT NUMBER |
| Product_Information        | PRODUCT GROUP CODE, EXCHANGE CODE, SYMBOL, EXPIRATION DATE    |
| Total_Transaction_Amount   | Net Total of the (QUANTITY LONG - QUANTITY SHORT)             |


Notes: 
- Each Record in the input file represents ONE Transaction from the client for a particular product.

---------

Technical Solution
-----

- Java 8
- Spring Boot
- Kafka
- PostgreSQL
- Junit 4
- Docker
- Docker Compose

How to use
----

The application can be started with a command line shell script:

- `bin/run.sh`

This script fires up all the required docker containers:

- `trading-db` : PostgreSQL database to store incoming trade transactions
- `trading-kafka` : Kafka single node cluster
- `trading-zookeeper` : Zookeeper service to keep track of Kafka nodes, topics, etc
- `trading-app`: Spring Boot app to process futures transactions   

The application can be run in 2 modes:

1. `Dev mode` : run from project root using - `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` runs at localhost:8080
2. `Prod / Docker mode` : the script `bin/run.sh` starts up everything needed running at port localhost:9090

The application exposes the following endpoints:

| HTTP Method  | Endpoint                           | Description                       |
|:-------------|:-----------------------------------|:----------------------------------| 
| GET          | /api/                              | Welcome message                   |
| POST         | /api/transactions                  | Upload futures transactions file  |
| GET          | /api/report/all                    | Generate summary report for all futures transactions by all clients  |
| GET          | /api/report/{clientNumber}         | Generate report (in JSON format) for all futures transactions done by a given client on a given date. Optional : `transaction date` as query param. If not provided returns all transactions done by a given client |
| GET          | /api/report/csv/{clientNumber}     | Generate report (in CSV format) for all futures transactions done by a given client on a given date. Optional : `transaction date` as query param. If not provided returns all transactions done by a given client |


The application logs are generated at:

- `logs/app.log` 

A sample for following artifacts can be found at `samples` directory:

- Application logs  :  `app.log`
- Input file        :  `Upload.txt`
- Output CSV file   :  `Output.csv`

The input and output files are stored at:

- `data/downloads` : generated output CSV file
- `data/uploads` : input file uploaded by the user



