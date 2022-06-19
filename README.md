# Dev Challenge (Short)

### The Asset Management Digital Challenge
Your task is to add functionality for a transfer of money between accounts. Transfers should be specified by providing:
 * accountFrom id
 * accountTo id
 * amount to transfer between accounts


I have added the basic functionality and added exceptions and logging for the same.
Points that can be improved on:
* Authentication can be added using JWT tokens.
* Currency handling.
* Global Exception handling for other requests as well.
* Logging can be improved for the whole project.
* Error messages and relevant messages can be externalized


### Create Account
    curl --location --request POST 'http://localhost:18080/v1/accounts/' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "accountId": "1",
    "balance": 300
    }'

### Get Account
    curl --location --request GET 'http://localhost:18080/v1/accounts/1'

### Transfer Money Between Account
    curl --location --request PUT 'http://localhost:18080/v1/accounts/transfer' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "accountFromId": "1",
    "accountToId": "2",
    "transferAmount": 100
    }'