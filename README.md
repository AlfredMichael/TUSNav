# TUSNav

## Overview (find the apk in the Release folder)
**TUSNav** is a crowdsourced indoor navigation app designed for students, staff, and visitors at the TUS Moylish Campus. This mobile application combines Google Maps for outdoor navigation with crowdsourced, step-by-step directions enhanced by images for indoor navigation. By leveraging community contributions, TUSNav provides an effective solution for navigating campus buildings without relying on expensive infrastructure like Bluetooth beacons or compromising building blueprints.

<p float="left">
  <img src="images/app-screenshot%20(1).png" width="250" alt="App Screenshot 1">
  <img src="images/app-screenshot%20(2).png" width="250" alt="App Screenshot 2">
  <img src="images/app-screenshot%20(3).png" width="250" alt="App Screenshot 3">
  <img src="images/app-screenshot%20(4).png" width="250" alt="App Screenshot 4">
  <img src="images/app-screenshot%20(5).png" width="250" alt="App Screenshot 5">
  <img src="images/app-screenshot%20(6).png" width="250" alt="App Screenshot 6">
  <img src="images/app-screenshot%20(7).png" width="250" alt="App Screenshot 7">
</p>


## Features
- **Registration and Login**: Secure Firebase Authentication ensures user access and contributor authentication.
- **Autologin**: Automatically logs in contributors if credentials are stored locally.
- **Indoor and Outdoor Navigation**: Google Maps handles outdoor navigation, while crowdsourced directions and images provide precise indoor navigation.
- **Notifications**: Alerts users when within 100 meters of their destination using the Haversine formula.
- **Rating System**: Enables users to rate contributed directions via a slider, ensuring quality and feedback.

## Permissions
TUSNav requires the following permissions for optimal functionality:
1. **Camera**: Capture images for landmark navigation.
2. **Location**: Access GPS data for accurate navigation.
3. **Notifications**: Send distance-based alerts.
4. **Internet**: Perform network operations, including fetching data from APIs.
5. **Media Access**: Read user-selected images and videos for directions.

## Identification of Needs
TUSNav addresses the common challenge of navigating large campus buildings and finding specific rooms, especially for newcomers or users facing relocated lectures. It utilizes crowdsourced knowledge for reliable indoor navigation and simplifies movement across campus.

## How It Works
1. **Outdoor Navigation**: Uses GPS data and Google Maps to guide users to building entrances.
2. **Indoor Navigation**: Crowdsourced step-by-step directions with visual landmarks ensure precise navigation within buildings.
3. **Contributor Features**: Registered users can add and rate directions to improve navigation quality.

## Technologies Used
- **Firebase Authentication and Storage**: Secure user access and image storage.
- **Google Maps API**: Outdoor navigation features.
- **Haversine Formula**: Distance calculations for notifications.
- **Device Media Cache**: Optimizes navigation image storage.
- **Mobile APIs**: Integrates camera, location, and media services.

## Important Note
1. Find the exported realtime database json file in the firebaseStruct folder.
2. The maps api key and `google-services.json` file have both been removed from this repository for security reasons. If you want to run this project, you will need to supply your own `google-services.json` file and enter your maps api key in the strings.xml file. You can generate your own `google-services.json` file by creating a new Firebase project.


## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/YourGitHubUsername/TUSNav.git
   cd TUSNav
   ```

2. Install dependencies:
   - Use Android Studio to load the project and manage dependencies.

3. Run the app:
   - Connect your Android device or emulator and launch the app.

## Contact
Feel free to reach out for inquiries or collaborations:
- Michael Alfred: success_fred@yahoo.com

---