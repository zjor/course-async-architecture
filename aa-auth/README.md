# Authorization Server

**Getting token**
```bash
http -f POST :8080/oauth2/token grant_type=client_credentials scope='profile' -a admin:s3cr3t
# or
TOKEN=$(http -f POST :8080/oauth2/token grant_type=client_credentials scope='profile' -a admin:s3cr3t | jq -r .access_token)
```
**Introspect token**
```bash
http -f POST :8080/oauth2/introspect token=${TOKEN} -a admin:s3cr3t
```

## User registration

1. Obtain initial token
```bash
TOKEN=$(http -f POST :8080/oauth2/token grant_type=client_credentials scope='client.create' -a registrar-client:s3cr3t | jq -r .access_token)
```

2. Register new user
```bash
http POST :8080/connect/register client_name=alice grant_types=authorization_code scope='openid,profile' -A bearer -a ${TOKEN}
```

## TODO

1. Persist users in the database
    - test endpoint for getting profile
2. Support user registration
3. Support roles
4. Support changing roles
5. Support user deletion