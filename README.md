# Serverless Hello World Java

The purpose of this project is to make a fast template to support more complex serverless applications using Java and Quarkus.

## Prerequisites

- [JDK 11](https://openjdk.org/projects/jdk/11)
- [Gradle](https://docs.gradle.org/current/userguide/userguide.html)
- [AWS CLI](https://aws.amazon.com/es/cli/) (only for deployment)

## How to build

Just execute

``` bash
./gradlew quarkusBuild
```

## How to run locally

Just run the following command to start a live-code development server:

``` bash
QUARKUS_LAMBDA_HANDLER="hello-world" ./gradlew quarkusDev
```

After that, you can start launching events as follows.
``` bash
curl -d @payload.json -X POST http://localhost:8080
```

Note that you can also set the `QUARKUS_LAMBDA_HANDLER` environment variable to `bye-world` to execute the second handler of this project.

## How to deploy

If you want to deploy the application on AWS, follow these steps:

1) Run the following by setting your lambda role RNA:
``` bash
LAMBDA_ROLE_ARN=XXXXXX ./manage_function.sh deploy ServerlessHelloWorldJava QUARKUS_LAMBDA_HANDLER=hello-world
```
2) Configure an API Gateway to point to your Lambda and you're done.
