up:
	docker-compose up -d

down:
	docker-compose down

start:
	mvn exec:java

config-database:
	mvn flyway:migrate

shell:
	docker exec -it database_mer mysql -uroot -p