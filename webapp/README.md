# Web app Readme

## Pre-Requisites 
1) Install IntelliJ
2) Install Postgresql Database on your local machine
3) Create database userdb using: Create database userdb;
4) Create table usertable under userdb using : create table usertable(username varchar(50), password varchar(200));
5) Add extension of Postman to your google chrome.

## Building And Deploying Application
1) Clone the project from the repository on your local machine. 
2) Make postgres configuration changes in application.properties file by entering your postgres credentials.
3) Run the project and hit the url from postman : "localhost:8080/user/register to register a user using POST method.
4) Go to the postman and hit the url: "localhost:8080/time to view the time using Get method.

## Instructions on exceuting the test
1) Open file, login application, and click run button to execute the test.

## Working of the application
1) The application is developed in Spring Boot framework with Rest APIs.
2) Its provide different API to view the current time and register a user.
3) The password is saved securely using BCrypt password hashing scheme with salt.
4) Validations implemented to ensure that username doesnot exist already and to check whether the user is registered, when we hit /time url from postman by checking 
   the credentials passed in basic auth header through postman, with the saved db credentials.
   
