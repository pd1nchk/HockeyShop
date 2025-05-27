# Admin Panel Implementation
ы
## Overview

The admin panel provides store administrators with the ability to manage products in the Hockey Shop application.

## Features Implemented

1. **Access Control**:
   - Admin panel is only accessible to users with the ADMIN role
   - Admin panel button appears in the top app bar of the HomeScreen for admins

2. **Products Management**:
   - View a list of all products with details
   - UI for adding new products (dialog with form fields)
   - UI for editing existing products
   - UI for deleting products with confirmation

## Components

- **AdminPanelScreen**: Main screen for admin functionality
- **AddEditProductDialog**: Dialog for adding and editing products
- **ConfirmationDialog**: General-purpose confirmation dialog used for deletion confirmation

## Future Improvements

1. **Backend Integration**:
   - Implement actual product creation/updating/deletion functionality
   - Connect to a product repository to perform CRUD operations

2. **Additional Management**:
   - Add category management (add, edit, delete categories)
   - Add order management (view and process orders)
   - Add user management (view, edit, and delete users)

3. **Enhancements**:
   - Image upload functionality for product images
   - Bulk product operations
   - Search and filtering options for products
   - Analytics and reporting for sales and inventory

## Usage

Admins can access the admin panel by clicking on the admin panel icon in the top app bar of the HomeScreen.
The admin panel provides a list of products with edit and delete options, and a floating action button to add new products. 