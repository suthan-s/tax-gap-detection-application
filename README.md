# tax-gap-detection-application

## This service is running with JAVA 17

The Tax Gap Application is a Spring Boot–based backend application designed to identify discrepancies between reported tax and expected tax for customer transactions.

## How to Run the Application

1. Clone the Project 
        git clone <repo-url>
2. Database Setup
      Create the database
      Update application.properties file - provide data base server username, password
3. Run the Application
      Run SpringBootApplication class

## DB setup instructions
   create data base tables
         transactions
         tax_rules
         exceptions_management
         audit_logs

## sample curl / postman calls
 
   Upload Transactions
        POST /upload
        URL: http://localhost:8080/tax/transaction/upload
   All exception & filter exception
        GET /findExceptions
        URL: http://localhost:8080/tax/transaction/findExceptions?severity=HIGH&ruleName=HIGH_VALUE_TRANSACTION_RULE 
   Exception summary report
        GET /exception-summary
        URL: http://localhost:8080/tax/transaction/exception-summary
   Customer tax summary report
        GET /customer-tax-summary
        URL: http://localhost:8080/tax/transaction/customer-tax-summary
