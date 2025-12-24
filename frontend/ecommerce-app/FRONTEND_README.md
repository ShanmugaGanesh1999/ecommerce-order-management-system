# Angular Frontend - E-Commerce Application

## Overview
This is an Angular 18 standalone components-based frontend for the E-Commerce Order Management System.

## Project Structure
```
src/app/
├── core/                    # Singleton services, guards, interceptors
│   ├── services/
│   │   ├── auth.service.ts          # Authentication service (JWT token management)
│   │   ├── api.service.ts           # Base HTTP service for API calls
│   │   ├── product.service.ts       # Product API service
│   │   ├── order.service.ts         # Order API service
│   │   ├── cart.service.ts          # Cart management service
│   │   └── storage.service.ts       # LocalStorage wrapper
│   ├── guards/
│   │   ├── auth.guard.ts            # Route guard for authenticated routes
│   │   └── admin.guard.ts           # Route guard for admin routes
│   ├── interceptors/
│   │   ├── auth.interceptor.ts      # JWT token interceptor
│   │   └── error.interceptor.ts     # Global error handler
│   └── models/
│       ├── user.model.ts            # User interfaces
│       ├── product.model.ts         # Product interfaces
│       ├── order.model.ts           # Order interfaces
│       └── cart.model.ts            # Cart interfaces
│
├── shared/                  # Reusable components
│   └── components/
│       ├── header/                  # App header with navigation
│       ├── footer/                  # App footer
│       ├── loading-spinner/         # Loading indicator
│       └── pagination/              # Pagination component
│
├── features/                # Feature modules
│   ├── auth/
│   │   └── components/
│   │       ├── login/               # Login page
│   │       ├── register/            # Registration page
│   │       └── profile/             # User profile page
│   ├── products/
│   │   └── components/
│   │       ├── product-list/        # Product listing with filters
│   │       ├── product-detail/      # Product detail page
│   │       └── product-search/      # Search component
│   ├── cart/
│   │   └── components/
│   │       └── cart/                # Shopping cart page
│   ├── orders/
│   │   └── components/
│   │       ├── checkout/            # Checkout page
│   │       ├── order-list/          # Order history
│   │       └── order-detail/        # Order detail page
│   └── admin/
│       └── components/
│           ├── dashboard/           # Admin dashboard
│           ├── product-manage/      # Product management
│           └── order-manage/        # Order management
│
├── app.component.ts         # Root component
├── app.config.ts            # Application configuration
└── app.routes.ts            # Route definitions
```

## Current Implementation Status

### Completed ✅
- Project initialized with Angular 18
- Bootstrap 5 installed
- Environment configuration (development and production)
- Model interfaces (User, Product, Order, Cart)
- Directory structure created
- Dockerfile and nginx.conf for production deployment
- Global styles with Bootstrap integration

### To Be Implemented 📋

#### Core Services
- **AuthService**: Login, register, logout, token management, auto-refresh
- **ApiService**: Base HTTP client with interceptors
- **ProductService**: Get products, search, filter, categories
- **OrderService**: Create order, get orders, update status
- **CartService**: Add/remove items, calculate total, persistence
- **StorageService**: LocalStorage wrapper for cart and auth data

#### Guards & Interceptors
- **AuthGuard**: Protect authenticated routes
- **AdminGuard**: Protect admin routes
- **AuthInterceptor**: Attach JWT token to requests
- **ErrorInterceptor**: Handle HTTP errors globally

#### Components

**Shared Components:**
- Header: Navigation, cart icon with badge, user menu
- Footer: Copyright and links
- Loading Spinner: Display during API calls
- Pagination: Reusable pagination controls

**Auth Components:**
- Login: Email/password form with validation
- Register: Registration form with validation
- Profile: View/edit user profile

**Product Components:**
- Product List: Grid/list view, filters (category, price), sorting, pagination
- Product Detail: Full product info, add to cart button
- Product Search: Search bar with debounce

**Cart Component:**
- Cart: Display items, update quantity, remove items, total calculation
- Proceed to checkout button

**Order Components:**
- Checkout: Address selection, order summary, place order
- Order List: Order history with status badges
- Order Detail: Full order information with items

**Admin Components:**
- Dashboard: Stats (total products, orders, revenue)
- Product Management: CRUD table for products
- Order Management: View orders, update status

#### Routing
```typescript
const routes: Routes = [
  { path: '', redirectTo: '/products', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'cart', component: CartComponent },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'orders',
    component: OrderListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'orders/:id',
    component: OrderDetailComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, AdminGuard]
  }
];
```

## Development Setup

### Prerequisites
- Node.js 20+
- Angular CLI 18+

### Installation
```bash
cd frontend/ecommerce-app
npm install
```

### Development Server
```bash
npm start
```
Navigate to `http://localhost:4200/`

### Build
```bash
npm run build
```

### Docker Build
```bash
docker build -t ecommerce-frontend .
docker run -p 4200:80 ecommerce-frontend
```

## API Integration

### Base URL
Development: `http://localhost:8080/api/v1`
Production: Configure in `environment.prod.ts`

### Authentication
- JWT tokens stored in localStorage
- Access token attached to all API requests via interceptor
- Refresh token used for token renewal
- Token expiry handled with automatic refresh

### API Endpoints Used
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/refresh` - Token refresh
- `GET /products` - Get products (with pagination, filters)
- `GET /products/{id}` - Get product by ID
- `GET /categories` - Get categories
- `POST /orders` - Create order
- `GET /orders/customer/{customerId}` - Get customer orders
- `GET /customers/{id}/profile` - Get customer profile
- `PUT /customers/{id}/profile` - Update customer profile

## Key Features

### User Features
- Browse products with search and filters
- View product details
- Add products to cart
- Place orders
- View order history
- Manage profile

### Admin Features
- View dashboard with statistics
- Manage products (CRUD operations)
- View and update order status
- Manage categories

## Technologies
- Angular 18 (Standalone Components)
- TypeScript (Strict Mode)
- Bootstrap 5
- SCSS
- RxJS for state management
- Reactive Forms
- HTTP Client with interceptors
- LocalStorage for cart persistence

## Best Practices
- Standalone components (no NgModules)
- Reactive forms with validation
- HTTP interceptors for auth and errors
- Route guards for security
- Lazy loading for performance
- Responsive design (mobile-first)
- Error handling with user feedback
- Loading states during API calls

## Next Steps
1. Implement core services (auth, API, product, order, cart)
2. Implement guards and interceptors
3. Create shared components (header, footer, spinner, pagination)
4. Implement auth components (login, register, profile)
5. Implement product components (list, detail, search)
6. Implement cart component
7. Implement order components (checkout, list, detail)
8. Implement admin components (dashboard, management)
9. Add comprehensive error handling
10. Add unit tests (Jasmine/Karma)
11. Add E2E tests (Cypress)
12. Performance optimization
13. Accessibility improvements (ARIA labels)
14. SEO optimization

## Estimated Effort
- Core services & interceptors: 8-10 hours
- Auth components: 6-8 hours
- Product components: 10-12 hours
- Cart & Order components: 10-12 hours
- Admin components: 8-10 hours
- Testing & polish: 8-10 hours
- **Total: 50-62 hours** of development work

## Notes
- All components should be standalone (no NgModules)
- Use reactive forms for all user input
- Implement proper error handling and loading states
- Follow Angular style guide and best practices
- Ensure responsive design works on mobile, tablet, and desktop
- Add proper ARIA labels for accessibility
- Implement pagination for large lists
- Cache API responses where appropriate
- Implement debounce for search inputs
- Show success/error toast notifications
- Confirm before destructive actions (delete, cancel order)
