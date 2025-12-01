# Project Overview  
**Project Name:** Domain's Event Lottery System  

**Description:**  
An Android app that manages events using Firebase.  
It supports three roles:  
- **Admin** – views, removes, and manages uploaded events, users, and images.  
- **Organizer** – creates events and uploads posters.  
- **Entrant** – joins event lotteries using QR codes.  

---

## Project Structure  

| Path | Description |
|------|--------------|
| Dev/ | Contains the Android app source code (MainActivity, fragments, and activities). |
| doc/ | Documentation files (UML, citations, license, and team info). |
| doc/Citations.pdf | References for online sources used. |
| doc/LICENSE.md | License file defining usage and distribution terms for this project. |
| doc/UML notes.docx | Written UML notes and explanations of relationships. |
| doc/UMLDomain.drawio-2.png | UML class diagram image for the app features and navigation structure. |
| doc/team.txt | Contains team members |
| .gitignore | Files and directories ignored by Git. |
| README.md | Project summary and navigation guide. |

---

## How to Use the App - Admin side 
- Open the project in Android Studio.
- In AccountSignup.java, under the Admin section, add your device ID to the dedicated array.
- You can find your device ID in Logcat after running the app once.
- When you run the app, it will automatically open the Admin dashboard.
- To access the Organizer or Entrant sections, remove your device ID from the array and rerun the app.
