package de.superchat.webhook;
/**
 * This module package is to serve as webhook microservice. Right now, webhook is created
 * by this service only have one type 'AN'(Anonymous)
 *
 * DB access: this service owns 'webhook' table.
 *
 * Main functionalities:
 * 1. Generate webhook. To simplify, no secret create, we directly use its uuid.
 * 2. Delete webhook.
 * 3. Receive raw message from external sources.
 *
 */