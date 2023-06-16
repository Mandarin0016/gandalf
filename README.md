
# Gandalf

 **Gandalf** is a Java application built using **Java 17**, **JDA** (Java Discord API), and **MySQL**. It serves as a versatile bot for the Discord Servers of SoftUni (Software University), providing various functionalities to manage exams and ensure a fair and secure exam environment.

## Features
### Exam Related Activities
**Gandalf** takes care of **locking** all Discord channels during exam dates to maintain the integrity of the examinations.
It **notifies** all students about upcoming exams and ensures that the channels are inaccessible to prevent cheating. 
This feature relieves the burden on Discord moderators, as they no longer need to manually manage channel lockdown/unlock and exam schedules.
The bot allows administrators to **insert** exam-related data into the **MySQL** database.
When an upcoming exam is scheduled using the **/exam-notify** command, the bot takes care of notifying students and locking down all channels to prevent any cheating.
The `/lock` command can be used to manually lock all channels, ensuring the normal conduct of an exam. 
This command restricts user activity and prevents any interactions within the locked channels.
Once the exam is completed or the need for channel lockdown ceases, the `/unlock` command can be used to unlock all channels. 
This command restores normal access and enables users to resume their regular activities within the server.

### Trivia Game Functionality
Gandalf offers an exciting Trivia game feature to engage the community. 
The Trivia game includes a collection of questions stored in a JSON file, which are then inserted into the database. 
Players can participate in the Trivia game, earn points, and compete for a spot on the **global ranklist**. 
The game is designed to challenge their knowledge and entertain them. 
The `/trivia-start` command initiates a trivia challenge with the specified parameters:
Users can test their knowledge, answer the trivia questions, and accumulate points. 
They can check their trivia points at any time using the `/points` command.

## Commands

| Command            | Description                                                                                                     | Parameters                                                                                                                                                  |
| ------------------ | --------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| /rank-list         | Retrieve rank-list with the given flag: global, java, c#, python, js                                             | - flag: A string representing the desired rank-list flag. Possible values: global, java, c#, python, js                                                     |
| /latest-update-notify | Notify the latest update.                                                                                       | - version: The version for which the update notification will be.<br>- details: Any additional details, if you have.                                        |
| /trivia-start      | Start a trivia challenge now!                                                                                   | - group: The group you start the trivia for. Possible values: java, js, python, c#<br>- complexity: The complexity of the questions. Possible values: easy, medium or hard<br>- count: The number of questions you want the trivia to have. Possible values: 1-10 |
| /points            | Check your trivia points.                                                                                        | No parameters required                                                                                                                                      |
| /json-read         | Only for internal use.                                                                                          | - lang: The language for which to read the JSON file. Possible values: js, c#, js, python<br>- version: The version of the JSON file. Possible values: 1+ |
| /logging-member-removal | Enable/Disable the logging for member removal event occurrence.                                                | - status: The status to move on.                                                                                                                            |
| /exam-notify       | Notify the students about an upcoming exam and channel lockdown.                                                | No parameters required                                                                                                                                      |
| /exam-insert       | Insert an upcoming exam.                                                                                        | - course: The course that will perform the exam. Example: Programming Basics - 22 Април 2023<br>- start-date: The first day of the exam, should be Saturday. Example: 10-06-2023<br>- end-date: The end day of the exam, should be Sunday. Example: 11-06-2023 |
| /exam-list         | List all valid upcoming exams.                                                                                  | No parameters required                                                                                                                                      |
| /lock              | Lock all channels to ensure the normal conduct of the exam.                                                     | No parameters required                                                                                                                                      |
| /unlock            | Unlock all channels.                                                                                             | No parameters required                                                                                                                                      |
| /sync              | Sync all channels under every category.                                                                         | No parameters required                                                                                                                                      |

## Getting Started
To use Gandalf, you need to set up the required dependencies and configure the application. Follow the steps below to get started:

1. Clone the **Gandalf** repository to your local machine.
2. Install **Java 17** and **MySQL** on your system.
3. Set up a **Discord bot account** and obtain the **bot token**.
4. Configure the application by providing the necessary credentials and settings in the **configuration files**.
5. Import the required libraries and dependencies (**JDA**, **MySQL connector**, **etc**.) into your project.

## Build and run the application
For detailed instructions on setting up and running **Gandalf**, 
please contact an owner and ask for assistance:<br>
Discord account: **floyd_mandarin**<br>
Discord account: **kristian7007**<br>

## Contributing
Contributions to **Gandalf** are welcome! If you encounter any issues, have suggestions for improvements, or would like to add new features, please open an issue on the **GitHub** repository.

## License
Gandalf is released under the **MIT License**. You are free to use, modify, and distribute the code as per the terms of the license.

## Acknowledgements
We would like to express our gratitude to the contributors and the SoftUni community for their support and feedback in developing **Gandalf**.

Let **Gandalf** be your guardian and guide in managing Discord exams and creating an engaging Trivia experience for your community!