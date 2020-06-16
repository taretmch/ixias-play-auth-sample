# IxiaS Play Authentication Sample

This project is a seed project which includes implementations of play-authentication with IxiaS.

## Prerequirement

- Docker
- sbt

## Installation

### Invoke docker container and initialize database

```sh
% docker-compose up -d
```

### Invoke Play Application

```sh
% sbt
# for compilation
> compile
# for running
> run
```

Then, you can see play application on `localhost:9000`.
