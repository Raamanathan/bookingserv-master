
# How to Run
I used h2 database and hosted the application in `localhost:8080`.
During startup I'm adding 2 records to the DB from `data.sql` file.
created endpoint (GET) called `http://localhost:8080/booking/v1/bfs/fetchAll` to fetch all the records in DB.
created endpoint (POST) called `http://localhost:8080/booking/v1/bfs/booking` to create a record. this consumes only `application/json` format.

## Sample Post request 
```json
{
    "id":5,
    "first_name":"Paypal",
    "last_name":null,
    "date_of_birth":"1992-01-22",
    "checkin":"2022-02-02" ,
    "checkout":"2022-02-03",
    "totalprice":"4555",
    "deposit":"1222",
    "address": {
        "line1":"Senthil Nagar",
        "line2":"",
        "city":"Chennai",
        "state":"TN",
        "country":"IND",
        "zipcode":"600033"
    }
}
```

The above request is of type JSON.
Handled exceptions and performed few request validations.
## Database
`http://localhost:8080/h2-console`.
```
Driver Class : org.h2.Driver
JDBC URL : jdbc:h2:mem:dcbapp
User Name : sa
password : password
```
