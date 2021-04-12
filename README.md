## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!

# Resolution
## Step by Step (https://www.youtube.com/watch?v=ay6GjmiJTPM)
### Step 1
- Spend time reading documentation about the Kotlin language. I base myself on the guides offered by the official site https://kotlinlang.org/.
  Also informing me of the differences and similarities with other languages.
  
### Step 2
1. Download & Install IntelliJ Idea Community
2. Fork Project Antaeus
3. Open & Run project
4. Play with REST API

### Step 3
Time to spend in check the current implementation, and as a first iteration get draft design/implementation for solution.
As first interaction with the code, I added/implemented a simple new REST API service just a PoC, and try to invoke and verify that everything works.

### Step 4 - Design (https://www.youtube.com/watch?v=Ra7oqFqj9uU)
My decision is to introduce the change in the simplest way, minimizing code changes, given my little experience in Kotlin, and also in the time available (time to allocate given the current job).
As assumption, I assumed that scalability is an attribute not functional that is required.
Beside as the application is a REST API, I decided to use an external cron and expose a service that trigger the pending invoice payment, with that two tactics I will support a scheduler feature. The mainly reason is external cron offer us to mantain externally the configuration/execution of that, and also it avoids to have a  mechanisc when multiples instances(applications) trying to execute. Also expose this service as a REST API allow to execute when we believe that is best time, or well in case that all applications work behind a Load Balancer, the Load Balancer decided who takes the instance that minor CPUUtilization contain or well another metrics.






