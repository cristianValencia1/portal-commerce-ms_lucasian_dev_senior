FROM postgres:15.7

ENV POSTGRES_DB=commerce_platform
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

COPY sql/ /docker-entrypoint-initdb.d/

