# Snowplow Intern Task

This task was to create a JSON upload service for a JSON schema and validate the schema against JSON data.

## Running

Run this using [sbt](http://www.scala-sbt.org/).  You'll find a prepackaged version of sbt in the project directory:

Using bash script:
```bash
sbt "run 80"
```
Using Windows cmd:
```bash
sbt.bat "run 80"
```

Then the web application will be running on <http://localhost>.

You can then execute [cURL](https://curl.haxx.se/download.html) commands to upload the JSON schema file 'config-schema.json'.

Using bash script:
```bash
curl http://localhost/schema/config-schema -X POST -d @config-schema.json
```
Using Windows cmd:
```cmd
curl.exe http://localhost/schema/config-schema -X POST -d @config-schema.json
```

## Controllers

- schemaController.scala:

  The controller to handle JSON schemas.

