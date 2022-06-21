# SpringAccountProject
Spring AccountProject


## 계좌 API

1. 유저 생성
- Request
  - requestURL : http://localhost:8080/account/create-user
  - HTTP METHOD : POST
  - Body : raw(json)
```json
{
    "name": "nyh",
    "privateNumber": "0001000100"
}
```

- Response
```json
{
    "id": 1,
    "name": "nyh",
    "privateNumber": "0001000100",
    "registeredAt": "2022-06-21T20:06:41.6626601"
}
```

2. 계좌 생성
- Request
  - HTTP METHOD : POST
  - requestURL : http://localhost:8080/account/create-account
  - Body : raw(json)
```json
{
  "userId": 1,
  "initialBalance": 50000
}
```

- Response
```json
{
    "userId": 1,
    "accountNumber": "1000000000",
    "registeredAt": "2022-06-21T20:07:16.1417518"
}
```

3. 계좌 해지
- Request
  - HTTP METHOD : PUT
  - requestURL : http://localhost:8080/account/unregister-account/
  - Body : raw(json)
```
{
  "userId": 1,
  "accountNumber": 1000000001
}
```

- Response
```
{
    "userId": 1,
    "accountNumber": "1000000000",
    "registeredAt": "2022-06-21T20:07:16.1417518"
}
```

4. 계좌 확인
- Request
  - HTTP METHOD : GET
  - requestURL : http://localhost:8080/account/get/{user_id}
  - Body : raw(json)
- Response
```json
{
    "accountList": [
        {
            "accountNumber": "1000000000",
            "balance": 50000
        }
    ]
}
```


## 트랜잭션 API
1. 잔액 사용
- Request
  - HTTP METHOD : POST
  - requestURL : http://localhost:8080/transaction/use
  - Body : raw(json)
```json
{
    "userId":1,
    "accountNumber":"1000000000",
    "amount": 1000
}
```

- Response
```json
{
    "userId": null,
    "accountNumber": null,
    "transactionResult": "S",
    "transactionId": "8e6d10ed52fd438d81672b546802584e",
    "amount": 1000,
    "transactedAt": "2022-06-21T20:12:04.1727453"
}
```

2. 잔액 사용 취소
- Request
  - HTTP METHOD : POST
  - requestURL : http://localhost:8080/transaction/cancel
  - Body : raw(json)
 ```json
 {
    "transactionId":"8e6d10ed52fd438d81672b546802584e",
    "accountNumber":"1000000000",
    "amount":1000
}
 ```
 
 - Response
```json
{
    "accountNumber": null,
    "transactionResult": "S",
    "transactionId": "3a7745b24edf41eab96824e4f901164c",
    "amount": 1000,
    "transactedAt": "2022-06-21T20:12:56.7205807"
}
```

3. 잔액 사용 확인
- Request
  - HTTP METHOD : GET
  - requestURL : http://localhost:8080/transaction/{transaction_id}

- Response (cancel)
```json
{
    "accountNumber": null,
    "transactionType": "CANCEL",
    "transactionResult": "S",
    "transactionId": "3a7745b24edf41eab96824e4f901164c",
    "amount": 1000,
    "transactedAt": "2022-06-21T20:14:56.7860617"
}
```

- Response (use)
```json
{
    "accountNumber": null,
    "transactionType": "USE",
    "transactionResult": "S",
    "transactionId": "a2964337eff34c15b22b57931809b74d",
    "amount": 1000,
    "transactedAt": "2022-06-21T20:15:36.8282946"
}
```
