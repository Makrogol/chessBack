# chessBack

## Command

### For build and run service
docker build . --tag=chessback
ducker run -p 80:80 chessback  --name chessback\

### For build and run database
docker run -itd -e POSTGERS_USER=makrogol -e POSTGRES_PASSWORD=1234 -p 5432:5432 -v posthres_data:/var/lib/postgresql --name postgresql postgres:latest

### For connect to postgres with psql
psql -h 127.0.0.1 -p 5432 -U makrogol

### For docker container manage and system prune
Remove all unused containers\
docker system prune -a

Stop image\
docker stop IMAGE_NAME

Remove image\
docker rm IMAGE_NAME

List of all images\
docker ps -a

## docker-compose commands

### For first run
Up all containers\
docker-compose up --build

(add -d options for daemon mode)

Down all containers\
docker-compose down -v

Configure postgres\
docker-compose exec chessback alembic upgrade head

### For update only backend and don't close postgres
Stop backend\
docker-compose stop chessback

Remove container\
docker-compose rm chessback

Pull changes\
git pull

Build new backed container and run it\
docker-compose up --build chessback

Update postgres configure if need\
docker-compose exec chessback alebmic upgrade head
