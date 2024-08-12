# Workplace Reservation Bot

A bot to save you time when booking a place in the office you don't want to go to.

## Prerequisites

- [JDK 11](https://openjdk.org/projects/jdk/11)
- [Gradle](https://docs.gradle.org/current/userguide/userguide.html)
- [AWS CLI](https://aws.amazon.com/cli/) (only for deployment)

## How to Build

Execute the following command:

```bash
./gradlew quarkusBuild
```

## How to Run Locally

Run the following command to start a live-code development server:

```bash
QUARKUS_LAMBDA_HANDLER="telegram-handler" ./gradlew quarkusDev
```

After that, you can start launching events as follows:

```bash
curl -d @payload.json -X POST http://localhost:8080
```

Note that you can also set the `QUARKUS_LAMBDA_HANDLER` environment variable to `bye-world` to execute the second handler of this project.

## How to Deploy

If you want to deploy the application on AWS, follow these steps:

1. Run the following command, setting your Lambda role ARN:

```bash
LAMBDA_ROLE_ARN=XXXXX ./manage_function.sh deploy WorkplaceReservationBot QUARKUS_LAMBDA_HANDLER=telegram-handler,TELEGRAM_BOT_TOKEN=YYYYY
```

2. Configure an API Gateway to point to your Lambda, and you're done.

## Handlers

The bot consists of two handlers that can be deployed independently:

1. **Telegram Handler**: Acts as a webhook to receive interactions from a Telegram bot.
2. **Schedule Handler**: Schedules booking events.

### Telegram Handler

The `TelegramHandler` processes incoming messages from the Telegram bot and dispatches commands accordingly.

Allowed commands are:

| Command                        | Description                                      |
|--------------------------------|--------------------------------------------------|
| `/bookParking <date>`          | Books a parking space for the specified date.    |
| `/getOfficeAvailability <date>`| Retrieves office availability for the specified date. |
| `/getParkingAvailability <date>`| Retrieves parking availability for the specified date. |
| `/getOfficeBookings`           | Retrieves current office bookings.               |
| `/getParkingBookings`          | Retrieves current parking bookings.              |

### Schedule Handler

The `ScheduleHandler` schedules and executes booking commands at specified times. It only allows booking parking spaces with the offset specified in days, as configured in the [`application.yaml`](src/main/resources/application.yaml) file under `schedule->parking->offset`.

## Configuration

The [`application.yaml`](src/main/resources/application.yaml) file contains various configuration settings for the bot. Here are the main sections:

| Section                          | Key                                      | Description                                      |
|----------------------------------|------------------------------------------|--------------------------------------------------|
| **Telegram API Configuration**   | `telegram.api.hostname`                  | The hostname for the Telegram API.               |
|                                  | `telegram.bot.token`                     | The token for the Telegram bot.                  |
|                                  | `telegram.bot.default-reporting-channel` | The default channel for reporting.               |
| **Booking Platform Configuration**| `bookingPlatform.hostname`               | The hostname for the booking platform.           |
|                                  | `bookingPlatform.login.user`             | The login username for the booking platform.     |
|                                  | `bookingPlatform.login.password`         | The login password for the booking platform.     |
|                                  | `bookingPlatform.matching-tag.office`    | The tag for matching office bookings.            |
|                                  | `bookingPlatform.matching-tag.parking`   | The tag for matching parking bookings.           |
|                                  | `bookingPlatform.schedule.parking.offset`| The offset for parking schedule.                 |
|                                  | `bookingPlatform.booking-behaviour.max-parallel-bookings`| The maximum number of parallel bookings.         |
|                                  | `bookingPlatform.booking-behaviour.book-timeout`| The timeout for booking.                         |
|                                  | `bookingPlatform.booking-preferences`    | The booking preferences.                         |

## License

This project is licensed under the GNU General Public License v3.0. See the `LICENSE` file for more details.
