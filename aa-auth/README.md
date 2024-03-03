# Authorization Server

## Usage

**Create user**
```bash
http :8080/api/v1/auth/register login=alice@example.com password=1q2w3e4r role=ADMIN
```

**Login**
```bash
http :8080/api/v1/auth/login login=alice@example.com password=1q2w3e4r
```

**Change role**
```bash
TOKEN=$(http :8080/api/v1/auth/login login=alice@example.com password=1q2w3e4r | jq -r .token)
http PUT :8080/api/v1/auth/role role=WORKER -A bearer -a ${TOKEN}
```

**Verify token**
```bash
http GET :8080/api/v1/auth/verify -A bearer -a ${TOKEN}
```

**Logout**
```bash
http POST :8080/api/v1/auth/logout -A bearer -a ${TOKEN}
```