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
