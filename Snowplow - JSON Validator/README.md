# Snowplow Intern Task

This task was to create a JSON upload service for a JSON schema and validate the schema against JSON data.

## Running
This is assuming [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [cURL](https://curl.haxx.se/download.html) are already installed on your system.
Run this using [sbt](http://www.scala-sbt.org/).  You'll find a prepackaged version of sbt in the project directory.

Open a terminal in the project directory and issue the following for your system:

Using bash script:
```bash
sbt "run 80"
```
Using Windows cmd:
```bash
.\sbt.bat "run 80"
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

You can also execute [cURL](https://curl.haxx.se/download.html) commands to download the JSON schema file 'config-schema.json'.

Using bash script:
```bash
curl http://localhost/schema/config-schema -X GET -d @config-schema.json
```
Using Windows cmd:
```cmd
curl.exe http://localhost/schema/config-schema -X GET -d @config-schema.json
```

You can also execute [cURL](https://curl.haxx.se/download.html) commands to validate a JSON file 'config.json' against it's schema.

Using bash script:
```bash
curl http://localhost/validate/config-schema -X POST -d @config.json
```
Using Windows cmd:
```cmd
curl.exe http://localhost/validate/config-schema -X POST -d @config.json
```

All responses are in JSON format and appropriate to each condition.

## Controllers

- schemaController.scala:

  This controller handles the uploading of JSON files, downloading of the JSON Schema file and validating the data against the schema.
  
## Models
- schemaActions.scala:

  This model writes arguments to a class in Json format. This is used to return Json responses.
  
## Routes
- routes:
  This file defines all of the applications routes.
  
## Framework
- I used the [Play Framework](https://www.playframework.com/download) as from my research it is lightweight, fast and intuitive to use.  
  
## Dependencies/Libraries

- [JSON Schema Validator](https://github.com/daveclayton/json-schema-validator) was used as stated by the task spec.
- [Jackson Core](https://github.com/FasterXML/jackson-core) which is required for [JSON Schema Validator](https://github.com/daveclayton/json-schema-validator).

## Development Timeline & Notes

### General Notes
	Prior to this task I had never used Scala however I have used Object Orientated and Functional programming languages. 
	There was a slight learning curve to this task but from what I have accomplished so far has furthered my understanding of RESTful API's, working with the [Play Framework](https://www.playframework.com/download). and the Scala language. 
	I have also learned that Google is my best friend.

### File Locations
The files I worked on for this task are at the following:
- 	**Snowplow/conf/routes**
-	**Snowplow/app/controllers/schemaController.scala**
-	**Snowplow/app/models/schemaActions.scala**

### Timeline

- **04/05/2018** -
	Refined code to catch exceptions. Code tidy and commented. Updated README.md Is now fully functional.
	
- **03/05/2018** -
	Implemented validator fully. Just need to refine code and test fully.
	
- **02/05/2018** -
	Implemented JSON validation for the schema file.
	Currently implementing JSON data 'cleaning' which is the removal of null keys. Need to fix nested nulls.
	Focus will then be finishing validation of external library then code tidy.
	I also installed external lib and updated version of [Jackson Core](https://github.com/FasterXML/jackson-core) as required for json-schema-validator **_/lib/jackson-core-2.9.5.jar_**.

- **29/04/18** - 
	Completed the JSON Schema and Data files upload.
	Completed the JSON Schema download.
	Started template code for validation of JSONs.
	General code tidy and fixes.

- **28/04/18** - 
	Updated README.md to reflect where the project was at the current stage and what was 
	implemented.
	I then looked at the json schema validator and installed it for use as a library located at **_/lib/json-schema-validator-2.2.6-lib.jar_** to my task.

- **27/04/18** - 
	Starting work on the project. 
	I firstly researched [cURL](https://curl.haxx.se/download.html) and downloaded this to the project folder.

	I then looked into using a framework to aid implementation. I chose to use the Play framework as it looks easy to use, lightweight and very fast.

	After installing and setting up the Play framework I edited the **_conf/routes/_** file to edit the endpoints into the web application.

