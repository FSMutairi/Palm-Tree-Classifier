# NakhelAI – Palm Tree Classifier

Android app that identifies date palm varieties (**Khalas**, **Razzez**, **Shishi**) using an AI backend. Part of the Palm Tree Classifier graduation project.

## Features

- **Take Photo** – Capture a palm/date image with the camera
- **Upload File** – Choose an image from gallery (JPG/PNG)
- **Instant results** – Prediction with confidence score
- **Details** – Short descriptions for each variety (Khalas, Razzez, Shishi)
- **Dark / Light mode**
- **Arabic & English** – Localized UI and content
- **About** – Technical info about the model and project

## Supported Palm Varieties

| Variety | Description |
|--------|-------------|
| **Khalas** | Premium variety from Al-Qassim, Al-Kharj, Al-Ahsa; apricot-yellow, sweet; ~15–20% of Al-Ahsa palms. |
| **Shishi** | Large, brown/golden-yellow; ~17% moisture; very sweet; ~50% of Saudi date production. |
| **Razzez** | Historically dominant in Al-Ahsa; now rarer; medium-sized with visible outer skin. |

## Requirements

- **Android**: min SDK 24, target SDK 34
- **Kotlin** 1.8+
- A running **backend API** that exposes a `/predict` endpoint (image upload → JSON with `prediction`, `confidence`, `class_id`)

## Build & Run

1. Clone the repo:
   ```bash
   git clone https://github.com/FSMutairi/Palm-Tree-Classifier.git
   cd Palm-Tree-Classifier/PALM_ANDROID
   ```

2. Open the project in Android Studio (or use the command line).

3. Configure the API base URL (e.g. in `ApiClient.kt` or via build config) to point to your classifier backend.

4. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```
   Or use **Run** in Android Studio with a device or emulator (API 24+).

## Tech Stack

- **Kotlin** + **Android** (ViewBinding, Material Components)
- **Retrofit** + **OkHttp** – HTTP client and logging
- **Coil** – Image loading
- **Gson** – JSON (API responses)
- Backend: ensemble of **ConvNeXt Small** models (TFLite), with Test Time Augmentation (flip) for robustness

## Project Structure

```
PALM_ANDROID/
├── app/
│   ├── src/main/
│   │   ├── java/.../palmapp/     # MainActivity, ResultActivity, SettingsActivity, adapters, API
│   │   ├── res/                   # Layouts, drawables, strings (en/ar), themes
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## License

This project is licensed in accordance with the graduation project rules and guidelines set by the College of Computer Sciences and Information Technology (CCSIT).
