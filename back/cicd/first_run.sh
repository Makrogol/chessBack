sudo apt update
sudo apt install docker-compose

# TODO add here creating venv and in update_back.sh source venv/bin/activate

# Go to base dir
cd ..

# Create .env
touch .env
echo "# Set env variables here. For example DB_NAME, DB_PORT etc" >> .env

# Set data to .env file
nano .env

# Activate .env
source .env

# Create certs
mkdir certs
cd certs
openssl genrsa -out jwt-private.pem 2048
openssl rsa -in jwt-private.pem -outform PEM -pubout -out jwt-public.pem

# Run application
cd ..
docker compose up --build -d
docker compose exec chessback alembic upgrade head
docker attach chessback
