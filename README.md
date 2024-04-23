# JAVA-API

This API provides basic functionalities to manage user profiles in a Java-based system. Below you'll find the available commands for interacting with the API.

## Getting Started

Ensure your local server is running at `http://localhost:8080` before you begin using these commands.

## Commands

### Register a User

To register a new user, execute the following command:

```bash
curl -X POST http://localhost:8080/users/register \
-H "Content-Type: application/json" \
-d '{"username": "newuser", "password": "securepassword", "email": "user@example.com", "profileDescription": "Just a new user"}'
```
Note: After registering, note down the user ID returned by the server; this will be needed for subsequent commands.

### Retrieve a User Profile

To retrieve an existing user profile, use:

```bash
curl -X GET http://localhost:8080/users/USER_ID \
-u newuser:securepassword
```
Replace USER_ID with the actual ID of the user.

### Update a User Profile

To update a user's profile, execute:

```bash
curl -X PUT http://localhost:8080/users/USER_ID \
-H "Content-Type: application/json" \
-u newuser:securepassword \
-d '{"username": "newuser", "password": "newsecurepassword", "email": "updated@example.com", "profileDescription": "Updated profile description"}'
```

### Delete a User

To delete a user, run:

```bash
curl -X DELETE http://localhost:8080/users/USER_ID \
-u newuser:securepassword
```

## Upcoming Features

- **Database Integration:** Plan to integrate PostgreSQL for data management.
- **Persistent Login:** Implement JWT for persistent user authentication.
- **User Roles:** Support different roles such as admin and user.
- **Documentation:** Detailed API documentation is in progress.

