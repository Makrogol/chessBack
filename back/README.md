# chessBack

## Command

### For build and run service
```shell
docker build . --tag=chessback
ducker run -p 80:80 chessback  --name chessback\
```

### For build and run database
```shell
docker run -itd -e POSTGERS_USER=makrogol -e POSTGRES_PASSWORD=1234 -p 5432:5432 -v posthres_data:/var/lib/postgresql --name postgresql postgres:latest
```

### For connect to postgres with psql
```shell
psql -h 127.0.0.1 -p 5432 -U makrogol
```

### For docker container manage and system prune
Remove all unused containers
```shell
docker system prune -a
```

Stop image
```shell
docker stop IMAGE_NAME
```

Remove image
```shell
docker rm IMAGE_NAME
```

List of all images
```shell
docker ps -a
```

## docker-compose commands

### For first run
Up all containers
```shell
docker-compose up --build
```
(add -d options for daemon mode)

Down all containers
```shell
docker-compose down -v
```

Configure postgres
```shell
docker-compose exec chessback alembic upgrade head
```

### For update only backend and don't close postgres
Stop backend
```shell
docker-compose stop chessback
```

Remove container
```shell
docker-compose rm chessback
```

Pull changes
```shell
git pull
```

Build new backed container and run it
```shell
docker-compose up --build chessback
```

Update postgres configure if need
```shell
docker-compose exec chessback alebmic upgrade head
```


## Issue RSA private key + public key pair
```shell
# Generate an RSA private key, of size 2048
`openssl genrsa -out jwt-private.pem 2048`
```

```shell
# Extract the public key from the key pair, which can be used in a certificate
openssl rsa -in jwt-private.pem -outform PEM -pubout -out jwt-public.pem
```

## Save depends to requirements.txt with poetry
```shell
poetry export --without-hashes --format=requirements.txt > .\requirements.txt
```