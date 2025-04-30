FROM python:3.12
FROM alpine:edge

ENV LD_LIBRARY_PATH=/lib

RUN apk add --no-cache libstdc++6

COPY . .

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# Попробовать использовать эту штуку вместо комманд в cicd
#RUN alembic upgrade head
