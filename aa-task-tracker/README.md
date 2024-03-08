# Task Management Server

## Usage

**Create task**
```bash
TOKEN=$(http :8080/api/v1/auth/login login=alice@example.com password=1q2w3e4r | jq -r .token)
http :7001/api/v1/tasks/create description="Hello $(date)" -a ${TOKEN} -A bearer
```

**List tasks**
```bash
http GET :7001/api/v1/tasks -a ${TOKEN} -A bearer
```

**Reassign**
```bash
http POST :7001/api/v1/tasks/reassign -a ${TOKEN} -A bearer
```

**Set done**
```bash
http PUT :7001/api/v1/tasks/2/set-done status=true -A bearer -a ${TOKEN}
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