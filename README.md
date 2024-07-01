# Smart Luggage System

An Android application for controlling and monitoring a smart luggage device.

## Features

- **Follow Me**: Control luggage movement (forward, backward, left, right, autonomous mode)
- **Weight Detection**: Measure and display luggage weight
- **Location Tracking**: Get and display luggage location on a map
- **Packlist**: Set reminders and make packlist before the trip.

## Prerequisites

- Android Studio 4.0+
- Android SDK 21+
- Google Maps API key

## Installation

1. Clone the repository:
```git
git clone https://github.com/Haider-Jawadwala/SmartLuggageSystem
```
2. Open the project in Android Studio.

3. In `AndroidManifest.xml`, add your Google Maps API key:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE"/>
```
4. Build and run the application on your Android device or emulator.

Configuration
Update the smart luggage device's IP address and port in:
->FollowMeFragment.java
->WeightDetectionFragment.java
->LocationTrackingFragment.java

Replace the following line:
```java
socket = new Socket("192.168.168.177", 8000);
```
with your device's IP and port.

## Usage

### Follow Me
- Use directional buttons to control luggage movement
- Press "Auto" for autonomous mode

### Weight Detection
- Press "Measure Weight" to get current luggage weight

### Location Tracking
- Press "Get Location" to fetch and display luggage location on the map

### Packlist
- Set reminders and make packlist before the trip

## Project Structure

- `MainActivity.java`: Main activity hosting the fragments
- `FollowMeFragment.java`: Controls luggage movement
- `WeightDetectionFragment.java`: Measures luggage weight
- `LocationTrackingFragment.java`: Tracks luggage location
- `activity_main.xml`: Main layout file
- `fragment_follow_me.xml`: Layout for Follow Me fragment
- `fragment_weight_detection.xml`: Layout for Weight Detection fragment
- `fragment_location_tracking.xml`: Layout for Location Tracking fragment

