# SnackStack

Brown CS0320 Group Final Project: an application that helps users track inventory and recommend recipes

Tianhao Shi (tshi32)

Lanxi Li (llanxi)

Yan Han (hyan30)

Mariia Sidorova(msidorov)

https://github.com/SE-starshippilot/SnackStack

## Collaborators

This project is completed with the assistance of ChatGPT and Claude

## Design Choices

For frontend, we adopted the [Material Design UI](https://www.mdui.org/en/docs/2/components/checkbox), as it streamlined the designing of frontend components. Our frontend is based on React+Typescript. We used Clerk as authentication method.

For backend, we used Spark for routing requests from frontend and used PostgreSQL for storging data. We managed the migration of Data via Flyway. We used JDBI as a medium to connect Java and the Database, used gson library to serialize/deserialize json objects

The major functionality, the generation of data given user inventory, is done by LLM Service. We implemented the connection to OpenAI's API, as well as a method to self-host this service via Ollama.

### Frontend UI

![](/Users/shitianhao/Documents/SnackStack/imgs/ui_no_login.png)

This is our homescreen UI. When a user first access this page, they are viewed as a guest. They cannot view any of the functions, and if they try to do so, they will be asked to login. as below:
![](/Users/shitianhao/Documents/SnackStack/imgs/ui_ask_login.png)

After they successfully created and logged in, they can view the following functionalites:

![](/Users/shitianhao/Documents/SnackStack/imgs/inventory_management.png)

This is the inventory management page, where user can add, view and delete inventory items freely.

![](/Users/shitianhao/Documents/SnackStack/imgs/recipe_history.png)

This is the recipe history page, which records all the recipes user chose to cook previously. They can sort these recipes, searching them via keyword, like specific recipes by clicking the heart-shaped emoji to the right of each recipe, and filter the recipes showing only the ones they liked. They can also click the ice-cream icon to the left to view details of the recipe, including the detailed step description.

![](/Users/shitianhao/Documents/SnackStack/imgs/recipe_generation.png)

This is the recipe generation page, where user will be asked the serving size, meal type, preference, allergy information. The recipe will be displayed once generated.

### Backend logic

The backend files are sturctured in the following modules:
```bash
server
├── config
├── controller
├── dao
├── dto
├── exceptions
├── model
├── Server.java
├── service
└── utils
```



Our backend is designed in a layered fashion. In the top level we have the `controller` level logic, which accepts API requests from client. The controller logic is kept as simple as possible, and only extracts necessary parameters and calls for service layer logic to actually conduct the business transaction.

The `service` layer is responsible for manipulating with data and contacting different Data Access Objects (DAO) for data storage and perhaps LLM services.

The `model` contains POJOs (Plain Old Java Objects) which are basically a java record class object that represents how one row of a database table looks like.

The `dao` uses the `model` objects to construct connection to the database.

The `dto` stands for Data Transfer Object, and also represents how a different object is when its data is transferred between different layers of logic.

The `exceptions` module contains custon-defined exceptions, mostly related to exceptions occured during Database CRUD operations as well as LLM services.

The `config` is responsible to setup, configure and gracefully tear down resources we used during the application lifecycle, including building different layers, setting up connection to LLM service and DB. We managed all the resources used via the `ApplicationContext.java` class object, and setup different components using the `AppConfig.java`

### Database Design

![](/Users/shitianhao/Documents/SnackStack/imgs/db_schema.svg)

The above diagram displays our database design. We performed DB normalization and made sure it is holding 3NF.

## Setup

### Prerequisites

1. PostgreSQL Database
2. API key to OpenAI and Clerk
3. Optionally, ollama if you want to self-host generation of recipes

```bash
git clone https://github.com/SE-starshippilot/SnackStack.git
cd SnackStack
# Install npm dependencies for frontend:
cd Client
npm install
# Backend Setup
cd ../Server
# Perform Database Migration
mvn compile exec:java -Dexec.mainClass="com.snackstack.server.config.RunMigration
```

4. .env files: you can refer to the .env.example to config the env variables.

## Testing

### Frontend E2E Test

```bash
cd SnackStack/Client
npx playwright test
```

### Backend Unit Test

Open IntelliJ, navitgate to the test folder, and run through each file.

Also, when compiling the project, the test should have been ran through.
