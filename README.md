# Boardcamp - Back-end

"Boardcamp" is a REST API designed for the administration of boardgame rentals. This application enables users to register and manage customers, games, and rentals.

## API URL
API is available at: https://boardcamp-7s5r.onrender.com

## About 

Main features include:
- Register a new customer
- Register a new game
- Register a new rental
- List all games
- Retrieve details of a customer given an id
- List all rentals
- Finish a rental

## Available Endpoints

### Customer
|  HTTP Method | Route  | Description  |
|---|---|---|
| POST  | /customers | Register a customer, with name(string) and cpf(string) required  |
| GET  | /customers/:id  | Get the details of a customer given an id  |

### Game
|  HTTP Method | Route  | Description  |
|---|---|---|
| POST  | /games  | Register a game, with name(string), stockTotal(long) and pricePerDay(long) required and image(string) optional  |
| GET  | /games  | Get list of games  |

### Rental
|  HTTP Method | Route  | Description  |
|---|---|---|
| POST  | /rentals | Register a rental, with the required attributes: customerId, gameId, daysRentes  |
| GET  | /rentals  | Get list of rentals  |
| PUT  | /rentals/:id/return  | Finish a rental  |

## Technologies
<div style="display: inline_block">
   <img align="center" alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
   <img align="center" alt="express" src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white">
   <img align="center" alt="postgres" src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white">
</div>

## How to run for development

1. Clone this repository
2. Create a PostgreSQL database with whatever name you want
3. Configure the `.env` file using the `.env.example` file
4. Compile the package
```
mvn clean package
```
4. Run the ApiApplication class
```
    java -jar ./target/api-0.0.1-SNAPSHOT.jar
```


## How to run tests

1. Follow the steps 1- 4 in the last section
2. Configure the `.env.test` file using the `.env.example` file
3. Run test:

```
mvn test
```
