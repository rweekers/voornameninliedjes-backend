# voornameninliedjes-backend

The backend for the voornamen in liedjes application using a postgres backend.

## Running locally

Make sure you have a container runtime installed. Configure an environment variable `LASTFM_API_KEY`. If you have your own key you can use it, otherwise the [last.fm](https://www.last.fm/) integration will not work locally. Run `SongApplication` using the `dev` profile. Use `log` as well if you want to see your logging in the console.

[![Java CI with Maven](https://github.com/rweekers/voornameninliedjes-backend/actions/workflows/maven.yml/badge.svg)](https://github.com/rweekers/voornameninliedjes-backend/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rweekers_voornameninliedjes-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=rweekers_voornameninliedjes-backend)
