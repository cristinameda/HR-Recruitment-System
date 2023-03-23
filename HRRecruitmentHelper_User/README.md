DESCRIPTION

    This is the api that handles users.

API

	Add user
	Delete user
    Get one user by id or get all users page by page
	Check if a user exists
    Login & token validation
    Authentication and authorization using JWT Token


END-POINTS
    
    POST /users
        - User must be authenticated and to have ADMIN Role
        - Adds a new user
        - Response: 201

    GET /users
        - User must be authenticated
        - Returns user list
        - Response: 200
    
    GET /users/{id}
        - User must be authenticated
        - Returns user with given id
        - Response: 200

    DELETE /users/{id}
        - User must be authenticated and to have ADMIN Role
        - Deletes an user by id
        - Response: 204 NO CONTENT

    POST /users/email
        - Checks if email exists
        - Response 200

    POST /login
        - Generate jwt token if input credentials are valid
        - Returns the token and his validity.        
        - Response 200
    
    POST /validateToken
        - Checks is the token is valid
        - Response 200
SWAGGER

    /swagger-ui.html

RUN LOCALLY

    Create database.
    Run scripts from Database_user/HRRecruitmentHelper_User.sql to create tables.
    Connect the project to a PostgresSQL database using application.properties.

RUN TESTS

    Tests are written using JUnit5.
    You can run each test individually or run them all at once.
    