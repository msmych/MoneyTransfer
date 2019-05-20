#Money transfer

*REST API for transferring money between accounts*

### How to run
```
gradle run
```

### How to run tests
```
gradle test
```
### Challenge
Design and implement a RESTful API (including data model and the backing implementation)
for money transfers between accounts.
Explicit requirements:
1. You can use Java, Scala or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 – keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require
a pre-installed container/server).
7. Demonstrate with tests that the API works as expected.
Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.
Please put your work on github or bitbucket.

### API
#### Create account
```
POST account

Body: 
{ 
    id: string 
}
```
#### Get account
```
GET account?id={id}

Response:
{
    id: string,
    amount: number
}
```
#### Deposit
```
POST account/deposit

Body:
{
    accountId: string,
    amount: number
}
```
#### Withdraw
```
POST account/withdraw

Body:
{
    accountId: string,
    amount: number
}
```
#### Transfer
```
POST account/transfer

Body:
{
    sourceId: string,
    targetId: string,
    amount: number
}
```
#### Delete account
```
DELETE account?id={id}
```
