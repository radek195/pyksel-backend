#!/bin/bash

flyway \
  -url=jdbc:postgresql://localhost:5432/pyksel \
  -user=pyksel \
  -password=pyksel \
  -locations=filesystem:src/main/resources/db/migration \
  -driver=org.postgresql.Driver \
  migrate