sudo apt update
sudo apt install docker-compose
sudo apt install git

docker compose stop chessback
docker compose rm chessback
docker system prune -af

git pull

cd ..
source .env
docker compose up -d --build chessback
docker compose exec chessback alembic upgrade head
docker attach chessback
