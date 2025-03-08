sudo apt update
sudo apt install docker-compose
sudo apt install git

docker-compose stop chessback
docker-compose rm chessback
docker system prune -a

git pull
docker-compose up --build chessback
docker-compose exec chessback alebmic upgrade head
