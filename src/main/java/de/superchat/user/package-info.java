package de.superchat.user;
/**
 * This module package is to serve as user microservice.
 *
 * DB access: this user service share 'user' table with auth service.
 * Besides, it has access to 'user_info' table with additional data that does not need for internal communication
 * and authentication/authorization process.
 *
 * Main functionalities:
 * 1. List all internal Superchat users and dummy external Webhook users.
 * 2. Create new user. That could be serve as register function.
 *
 */