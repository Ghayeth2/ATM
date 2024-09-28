# ATM

## About 
An ATM System that simulates the core functionalities of a real ATM. The system allows users to perform essential banking operations, such as checking account balances, withdrawing funds, depositing money, and transferring funds between accounts.

In addition to basic user features, the system includes administrative capabilities that allow admins to manage user accounts, monitor transactions, and access detailed transaction logs to ensure security and transparency.

## Used Technologies
* **Java 17**
* **Spring Boot**
* **Spring Data JPA**
* **Spring Security**
* **Spring MVC**
* **Thymeleaf**
* **MySQL**
* **Redis**
* **MailDev**

## Technical Features
* **Bean Validation**: Ensures data integrity using annotations like `@Valid`, `@NotNull`, and `@Pattern`.
* **Email Verification / Password Reset**: Handles user registration verification and password recovery via email.
* **RESTful API / AJAX**: Supports dynamic content updates and REST-based backend interactions.
* **Paging & Sorting**: Efficiently manages large data sets with pagination and sorting capabilities using Spring Data.
* **Form-Login / OAuth2**: Provides both traditional form-based login and OAuth2 for third-party authentication.
* **Internationalization**: Offers multilingual support using message bundles.
* **Centralized Resource Bundles**: Organized resource bundles (`messages.properties`, `errors.properties`, `validations.properties`, etc.) for easy management of localized content.
* **Global Exception Handler**: Centralized error handling across the application using `@ControllerAdvice` and `@ExceptionHandler`.
* **Account Locking / Limited Login Attempts**: Implements security features such as account locking after multiple failed login attempts.
* **Auditing**: Tracks creation and modification timestamps using JPA auditing.
* **Design Patterns**: Utilizes **Strategy** and **Facade** patterns for better code organization and flexibility.

## Requirements for Installing the Project
* **Java 17**
* **Redis**
* **MySQL**
* **MailDev**

## Want to Contribute?
If you're interested in collaborating or contributing to this project, feel free to email me at [ghayeth.msri@gmail.com]. I look forward to working together!
