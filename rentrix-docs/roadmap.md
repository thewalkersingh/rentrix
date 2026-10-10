# Rent History Checker

## Overview

Rent History Checker is a web application for managing and reviewing rental properties. Built with Java and Spring Boot, it allows users to provide reviews, track ownership histories, and report issues related to flats.

## Features

1. **User Management**:
   - Register and manage user profiles.
   - Includes contact details and roles.

2. **Flat Management**:
   - Add, update, delete, and view flats.
   - Track availability, rooms, area, price, and descriptions.

3. **Review Management**:
   - Submit and view reviews for flats.
   - Reviews include title, content, rating, and date.

4. **Ownership History**:
   - Track ownership history of flats.
   - Record ownership periods linked to users and flats.

5. **Issue Reporting**:
   - Report and track issues related to flats.
   - Includes description, report date, and resolution status.

## Technology Stack

- **Backend**: Java, Spring Boot
- **Database**: MySQL
- **Build Tool**: Maven

## Entities

1. **User**: Unique ID, username, telephone, password, name, address, email, date of birth, role.
2. **Flat**: Unique ID, address, city, state, rooms, area, price, description, availability.
3. **Review**: Unique ID, user, flat, title, content, rating, review date.
4. **OwnershipHistory**: Unique ID, flat, owner, start date, end date.
5. **Issue**: Unique ID, flat, description, report date, resolve date, resolved status.

## Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/latelespaul/rentrix.git
   cd rentrix
