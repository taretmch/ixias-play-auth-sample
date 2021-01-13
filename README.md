# ixias-play-auth sample

This is a seed project using [ixias-play-auth](https://github.com/ixias-net/ixias/tree/develop/framework/ixias-play-auth).

## Prerequirement

- Docker
- sbt

## Installation

### Invoke docker container and initialize database

```sh
% docker-compose up -d
```

### Run Play Application

```sh
% sbt
# for compilation
> compile
# for running
> run
```

Then, you can see application on `localhost:9000`.

## To access MySQL

```
% docker-compose exec db bash
root@5d98941e3877:/# mysql -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 4
Server version: 5.7.30 MySQL Community Server (GPL)

Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| blog               |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.04 sec)

mysql>
```
