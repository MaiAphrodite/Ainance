# AInance - AI Financial Advisor

> Android finance tracker with savings goals and an LLM advisor built on your own data

[![License](https://img.shields.io/badge/license-MIT-2ea44f?style=flat-square)](LICENSE)
[![Stack](https://img.shields.io/badge/stack-Android%20%7C%20Java%20%7C%20Room-000?style=flat-square)](#tech-stack)

AInance is a single module Android app for recording income and expenses, setting
savings goals, and asking a chat model for financial advice based on the data already
on the phone. Everything except the AI request itself stays local: transactions and
goals live in Room, and settings live in SharedPreferences. It is a personal project,
not a published product, so there is no account system, sync, or analytics.

## Features

- **Transactions**: an add dialog with Income and Expense tabs, an amount, a
  description, and a category picked from a preset list.
- **Balance**: computed as total income minus total expenses. The dashboard shows the
  latest five transactions, while totals always use every row in the database.
- **Goals**: create a goal with a target amount, a type (Monthly, Yearly, Custom), and
  a target date. Progress is a percentage capped at 100 percent.
- **Goal payments**: paying into a goal records an Expense transaction, so the money
  leaves the main balance, and adds the same amount to the goal. The dialog refuses
  payments larger than the current balance.
- **AI advice**: the Ask AI button sends a summary (totals, up to ten recent
  transactions, goals with progress) to an OpenAI compatible chat completions
  endpoint and renders the reply as Markdown.
- **Settings**: currency symbol (default `Rp.`), API base URL, API key, and a reset
  that deletes all transactions, goals, and preferences.

## Tech stack

| Layer | Choice |
|-------|--------|
| Language | Java 11 source/target, view binding enabled |
| Platform | minSdk 29, targetSdk 35, compileSdk 35 |
| Build | Gradle 8.11.1 wrapper, AGP 8.9.0, version catalog in `gradle/libs.versions.toml` |
| UI | AppCompat 1.7.1, Material 1.12.0, ConstraintLayout 2.2.1, Navigation 2.9.0 |
| Data | Room 2.6.1 with an annotation processor, SharedPreferences |
| Network | OkHttp 4.12.0 and Gson 2.10.1 for the chat completions call |
| Tests | JUnit 4.13.2, AndroidX test runner, Espresso 3.6.1 |

## Getting started

### Prerequisites

- Android Studio with support for AGP 8.9.0 (the wrapper pins Gradle 8.11.1)
- Android SDK 35 installed
- A device or emulator on Android 10 (API 29) or newer
- The app compiles with Java 11 source and target compatibility, as set in
  `app/build.gradle.kts`

### Quick start in Android Studio

1. Open Android Studio, then **File > Open** and select this repository folder.
2. Let Gradle sync. It downloads Gradle 8.11.1 and the libraries listed in
   `gradle/libs.versions.toml`.
3. Choose the `app` run configuration and press **Run**.

Or build the debug APK from a terminal:

```bash
sh gradlew :app:assembleDebug
```

The wrapper script is committed without the executable bit, so call it through `sh`
instead of `./gradlew`.

### Configure the AI advisor

1. Tap the floating action button, then **Settings**.
2. Enter an API base URL. Leave it empty to use the OpenAI default
   (`https://api.openai.com/v1/chat/completions`), or point it at a local server such
   as `http://10.0.2.2:1234` from the emulator.
3. Enter an API key. It is required for `openai.com` endpoints and optional for local
   servers, which are detected by host and port (`localhost`, `127.0.0.1`, `192.168.`,
   `10.0.`, `172.16.`, ports 1234, 8080, 3000, and 5000).
4. Save, return to the dashboard, and tap **Ask AI**.

## How it works

### Balance and goals

- Main balance = total income minus total expenses.
- A goal payment is stored as an Expense transaction with the category `goal payment`,
  so it reduces the balance once and raises the goal's saved amount.
- Goals do not consume income automatically. Only explicit payments move them.

### AI request

`AIService` builds a chat completions request with a system prompt that carries the
financial summary and the user question, then reads the first choice from the JSON
response. The model name is fixed at `gpt-3.5-turbo`, so a local server has to accept
that name.

## Notes and limitations

- The API key is stored in plain SharedPreferences. Treat this as a personal build.
- The dashboard lists the latest five transactions only; there is no separate full
  history screen.
- Categories are fixed lists in `FormatUtils`. Settings changes the currency symbol,
  not the category lists.
- `SecondFragment` and its navigation action come from the Android Studio navigation
  template and are not reachable from the current UI.
- The model id is hard-coded in `AIService.java`. Change the constant if your endpoint
  needs a different model name.
- Export, import, charts, and budget alerts are not implemented.

## Contributing

Issues and pull requests are welcome. If you change behavior, say which device and API
level you tested on.

## License

MIT, see [LICENSE](LICENSE). Copyright (c) 2026 Mai.
