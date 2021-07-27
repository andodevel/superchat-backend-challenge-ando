package de.superchat.auth;
/**
 * This module package is to serve as auth microservice. Only internal Superchat user with
 * source 'SC' is allowed to do log in.
 *
 * DB access: this auth service share 'user' table with user service.
 *
 * Main functionalities:
 * 1. Exchange username/password for JWT token.
 *
 */