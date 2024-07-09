# Project: Loop

## Purpose
Loop is a real-time communication Android application developed in Java using Firebase. It aims to provide a secure and user-friendly interface for seamless communication between users.

## Project Overview

### Technology Stack
- **Language**: Java
- **Database & Authentication**: Firebase

### Features
- **Firebase Authentication**:
  - Allows user login using phone number and OTP with country code selection.
  
- **Firebase Firestore**:
  - Dynamically stores user data after successful login.
  - Stores chat history for peer-to-peer communication dynamically.

- **User Profile Management**:
  - Users can modify their username and profile photo.

## Installation Steps

### 1. Clone the Repository
```bash
git clone https://github.com/IJNikhil/Loop.git
cd loop/
```

### 2. Set Up Firebase Project
- Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/).
- Add an Android app to your Firebase project using package name `com.loop.app`.
- Download the `google-services.json` file and place it in the `app/` directory of your Android project.

### 3. Configure Firebase Dependencies
- Open `build.gradle` (Project Level) and add the Google Services classpath:
  ```groovy
  classpath 'com.google.gms:google-services:4.3.10'
  ```
- Open `build.gradle` (App Level) and apply the Google Services plugin at the bottom:
  ```groovy
  apply plugin: 'com.google.gms.google-services'
  ```

### 4. Run the Application
- Connect your Android device or use an emulator with minimum SDK version 28.
- Build and run the application from Android Studio.

## Required Files & Setup

### Project Structure
- **Files**: Ensure the following files are correctly set up and configured:
  - `google-services.json`: Firebase configuration file for Android.
  - `build.gradle` (Project Level): Ensure `google-services` classpath is included.
  - `build.gradle` (App Level): Apply `google-services` plugin and configure dependencies.

## Dependencies

### Android Dependencies
- **AndroidX Libraries**:
  ```groovy
  implementation 'androidx.appcompat:appcompat:1.6.1'
  implementation 'com.google.android.material:material:1.10.0'
  implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
  implementation 'androidx.recyclerview:recyclerview:1.3.2'
  ```
  
- **Firebase SDKs**:
  ```groovy
  implementation platform('com.google.firebase:firebase-bom:32.3.1')
  implementation 'com.google.firebase:firebase-analytics:21.5.0'
  implementation 'com.google.firebase:firebase-messaging:23.3.1'
  implementation 'com.google.firebase:firebase-firestore:24.9.1'
  implementation 'com.google.firebase:firebase-auth:22.3.0'
  implementation 'com.firebaseui:firebase-ui-firestore:8.0.2'
  implementation 'com.google.firebase:firebase-storage:20.3.0'
  ```

- **Other Libraries**:
  ```groovy
  implementation 'androidx.annotation:annotation:1.7.0'
  implementation 'androidx.legacy:legacy-support-v4:1.0.0'
  implementation 'com.hbb20:ccp:2.7.3'  // Country Code Picker
  implementation 'com.github.bumptech.glide:glide:4.16.0'  // For image loading
  implementation 'com.github.f0ris.sweetalert:library:1.6.2'  // Dialog
  implementation 'pl.droidsonroids.gif:android-gif-drawable:1.2.28'  // Gif Image
  implementation 'com.squareup.okhttp3:okhttp:4.11.0'  // OK Http
  implementation 'androidx.multidex:multidex:2.0.1'  // Multidex
  ```

## Conclusion
Loop is designed to offer efficient and secure real-time communication on Android, leveraging Firebase for robust backend services and a responsive user interface. Follow the steps above to set up and run the application on your development environment.
