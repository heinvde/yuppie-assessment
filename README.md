# Yuppie Chef Assessment

## Running local database

Using docker compose

```bash
# Start
docker-compose up -d
# Start with log tail
docker-compose up
# Stop
docker-compose down --volumes
# Stop and keep data
docker-compose down
```

Using docker

```bash
# Start
docker run \
  --env MYSQL_ROOT_PASSWORD=root \
  --env MYSQL_DATABASE=users \
  -v $(pwd)/databases/users:/docker-entrypoint-initdb.d \
  -p 3306:3306 \
  -d \
  mysql:5.7

# Stop
docker stop <container id>
```