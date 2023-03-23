### DESCRIPTION

    This is the project setup for the future api that will handle candidates.

### API

Different roles have different permissions for endpoints:

    HR Representative
        - insert a new candidate
        - view and filter candidates
        - schedule interviews for candidates
        - add HR recruiters and technical interviewers to a scheduled interview
        - give feedback to a candidate(go/ no go)
        - export candidate profile to PDF
        - archive a candidate profile

    Technical interviewer
        - view assigned candidates
        - view candidate CV's
        - leave a comment to a candidate profile
        - give feedback to a candidate(go/ no go)

    PTE
        - view and filter candidates
        - give the final feedback and archive the candidate profile
    
    Admin 
        - delete candidates

### SET UP

    This project uses Maven as building tool.
    Maven is downloadable as a zip file at

[https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

    Only the binaries are required, so look for the link to apache-maven-{version}-bin.zip or apache-maven-{version}-bin.tar.gz.
    Once you have downloaded the zip file, unzip it to your computer. Then add the bin folder to your path.
    To test the Maven installation, run mvn from the command-line: 

`xmvn -v`

### BASE URL

    http://localhost:8081

The paths section defines the individual endpoints and the HTTP methods supported by these endpoints.

    /candidates:
        POST:
            summary: Adds a new candidate
            responses:
                200
                description: OK

    /candidates:
        GET:
            summary: Retrieves all existing candidates
            responses:
                201
                description: CREATED

    /candidates/candidateId:
        GET:
            summary: Retrieves an existing candidate by ID
            parameters:
                - in: path
                    name: candidateId
                    required: true
                    type: BigInteger
            responses:
                200
                description: OK

    /candidates/id:
        DELETE:
            summary: Deletes an existing candidate by ID
            parameters:
                - in: path
                    name: id
                    required: true
                    type: BigInteger
            responses:
                204
                description: NO CONTENT

    /candidates/id:
        PUT:
            summary: Assigns users to a candidate
            parameters:
                - in: path
                    name: id
                    required: true
                    type: BigInteger
                - in: body
                    name: assignedUsersToCandidateDTO
                    description: The users to be assigned
                    schema:
                        type: list
                        properties:
                        - email:
                            type: String
                        - roleName:
                            type: String
            responses:
                204
                description: NO CONTENT

    /interview:
        POST:
            summary: Schedules a new interview
            parameters:
                - in: body
                    name: interviewDTO
                    description: The interview to be scheduled
                    schema:
                        type: object
                        properties:
                        - candidateId:
                            type: BigInteger
                        - dateTime:
                            type: LocalDateTime
                        - location:
                            type: String
            responses:
                201
                description: CREATED

### CONSUMES, PRODUCES

**Consumes:**

- application/json

**Produces:**

- application/json

### SWAGGER

[swagger-ui](http://localhost:8081/swagger-ui/index.html)

### RUN LOCALLY

    Create database on pgAdmin platform.
    Run script from database_candidate/script.sql to create tables.
    Connect the project to the PostgresSQL database using application.properties.

### RUN TESTS

    Tests are written using Junit5.
    You can run each test individually or run all test: test -f pom.xml