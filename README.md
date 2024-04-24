# JAVA-API

This API provides basic functionalities to manage user profiles in a Java-based system, allowing for both administrative and regular user interactions. It is designed to handle user registration, profile management, and role-based access controls, as well as file upload and download functionalities.

## User Roles and Permissions

- **Admin Users**: Admins have the ability to modify any user's profile, delete any account, and manage user roles, including granting and revoking admin rights. This allows admins to perform broad management tasks across the entire user base, as well as upload and download files.
- **Regular Users**: Regular users are limited to viewing and modifying their own profiles. They cannot access or modify other users' information, nor can they manage roles. This ensures that users can maintain privacy and control over their own information without affecting others. Regular users can download files but cannot upload them.
- **Non-Users**: Non-users are not allowed to perform any actions on the system.

## Getting Started

Ensure your local server is running at `http://localhost:8080` before you begin using these commands.

## Commands

### Register a User as Regular

To register a new user without admin privileges, execute the following command:

```bash
curl -X POST 'http://localhost:8080/users/register' \
-H "Content-Type: application/json" \
-d '{"username": "regularuser", "password": "securepassword", "email": "user@example.com", "profileDescription": "Just a regular user"}'
```
### Register a User as Admin

To register a new user with admin privileges by using an admin secret, use:

```bash
curl -X POST 'http://localhost:8080/users/register?adminSecret=secret' \
-H "Content-Type: application/json" \
-d '{"username": "adminuser", "password": "securepassword", "email": "admin@example.com", "profileDescription": "Administrator account"}'
```
### Retrieve a User Profile

To retrieve an existing user profile, replace `USER_ID` with the actual ID of the user:

```bash
curl -X GET 'http://localhost:8080/users/USER_ID' \
-u username:password
```
### Update a User Profile

To selectively update a user's profile information, such as email or profile description:

```bash
curl -X PATCH 'http://localhost:8080/users/USER_ID' \
-H "Content-Type: application/json" \
-u username:password \
-d '{"email": "newemail@example.com"}'
```
### Admin Granting Admin Role

For an admin to grant another user admin privileges:

```bash
curl -X PUT 'http://localhost:8080/users/admin/USER_ID/grant-admin' \
-u adminuser:securepassword
```
### Admin Revoking Admin Role

For an admin to revoke admin privileges from another user:

```bash
curl -X PUT 'http://localhost:8080/users/admin/USER_ID/revoke-admin' \
-u adminuser:securepassword
```
### Delete a User

To delete a user, ensure you have appropriate permissions (admin for any user, individual users can only delete their own profiles):

```bash
curl -X DELETE 'http://localhost:8080/users/USER_ID' \
-u username:password
```
### Upload a File

To upload a file, only admins can perform this action:

```bash
curl -X POST 'http://localhost:8080/files/upload' \
-u adminuser:securepassword \
-F "file=@/path/to/file"
```
### Download a File

To download a file, only authenticated users can perform this action:

```bash
curl -X GET 'http://localhost:8080/files/download/FILENAME' \
-u username:password \
-o downloaded_file.ext
```
## Upcoming Features

- **Acces Denied Message** Not all case and features have a propre acces denied msg (even if there is no msg, the acces denied is manage for all case).
- **Database Integration:** Plan to integrate PostgreSQL for robust data management.
- **Vault for Sensitive Data:** Implementing a vault for securely managing the admin secret.
- **Persistent Login:** Incorporating JWT for persistent user authentication and session management.
- **Documentation:** Detailed API documentation is underway, aiming to provide comprehensive usage and technical details.
