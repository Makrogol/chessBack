sudo apt update
sudo apt install docker-compose

# Go to base dir
cd ..

# Create .env
touch .env

# Create certs
mkdir certs
cd certs
openssl genrsa -out jwt-private.pem 2048
openssl rsa -in jwt-private.pem -outform PEM -pubout -out jwt-public.pem

# Run application
cd ..
docker-compose up --build
docker-compose exec chessback alembic upgrade head
