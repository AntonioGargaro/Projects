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

  This controller handles the uploading of JSON files and downloading of the JSON Schema file.
  
## Dependencies/Libraries

- [JSON Schema Validator](https://github.com/daveclayton/json-schema-validator) was used as stated by the task spec.

## Development Timeline & Notes

# General Notes
	Prior to this task I had never used Scala however I have used Object Orientated and Functional programming languages. 
	There was a slight learning curve to this task but from what I have accomplished so far has furthered my understanding of RESTful API's, working with frameworks and the Scala language. 
	I have also learned that Google is my best friend.

- 29/04/18
	Completed the JSON Schema and Data files upload.
	Completed the JSON Schema download.
	Started template code for validation of JSONs.
	General code tidy and fixes.

- 28/04/18
	Updated README.md to reflect where the project was at the current stage and what was 
	implemented.
	I then looked at the json schema validator and installed it for use as a library located at **_/lib/json-schema-validator/master/_** to my task.

- 27/04/18
	Starting work on the project. 
	I firstly researched [cURL](https://curl.haxx.se/download.html) and installed this into my system32 folder.

	I then looked into using a framework to aid implementation. I chose to use the Play framework as it looks easy to use, lightweight and very fast.

	After installing and setting up the Play framework I edited the **_conf/routes/_** file to edit the endpoints into the web application.

