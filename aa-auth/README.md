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

**Delete account**
```bash
http DELETE :8080/api/v1/auth/delete -A bearer -a ${TOKEN}
```

## Kafka usage

**Connect to `auth.accounts` topic**
```bash
SERVER=localhost:9092
${KAFKA_HOME}/bin/kafka-console-consumer.sh \
    --bootstrap-server ${SERVER} \
    --topic auth.accounts \
    --group group-alpha \
    --from-beginning
```