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
git clone https://github.com/your-username/loop.git
cd loop/


2. **Set Up Firebase Project**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app to your Firebase project using package name `com.loop.app`.
   - Download the `google-services.json` file and place it in the `app/` directory of your Android project.

3. **Configure Firebase Dependencies**
   - Open `build.gradle` (Project Level) and add the Google Services classpath:
     ```groovy
     classpath 'com.google.gms:google-services:4.3.10'
     ```
   - Open `build.gradle` (App Level) and apply the Google Services plugin at the bottom:
     ```groovy
     apply plugin: 'com.google.gms.google-services'
     ```

4. **Run the Application**
   - Connect your Android device or use an emulator with minimum SDK version 28.
   - Build and run the application from Android Studio.

### Required Files & Setup

#### Project Structure
- **Files**: Ensure the following files are correctly set up and configured:
  - `google-services.json`: Firebase configuration file for Android.
  - `build.gradle` (Project Level): Ensure `google-services` classpath is included.
  - `build.gradle` (App Level): Apply `google-services` plugin and configure dependencies.

#### Dependencies
- **Android Dependencies**: Listed in the previous section, these include AndroidX libraries, Firebase SDKs, Glide for image loading, and other utility libraries.

### Conclusion
Loop is designed to offer efficient and secure real-time communication on Android, leveraging Firebase for robust backend services and a responsive user interface. Follow the steps above to set up and run the application on your development environment.

```

This Markdown file now includes installation steps, required files, and setup instructions for your project "Loop". It provides a comprehensive guide for anyone looking to clone, configure, and run the application on their development environment. Adjust the Firebase setup steps based on your specific Firebase project configuration.