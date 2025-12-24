import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card mt-5">
            <div class="card-body">
              <h2 class="card-title text-center mb-4">Login</h2>
              <div class="alert alert-info">
                <h5>Frontend Implementation Needed</h5>
                <p>This is a placeholder component. The full implementation should include:</p>
                <ul>
                  <li>Email/password form with validation</li>
                  <li>Form submission to <code>POST /auth/login</code></li>
                  <li>Store JWT token in localStorage</li>
                  <li>Navigate to products after successful login</li>
                  <li>Error handling with user feedback</li>
                  <li>Link to registration page</li>
                </ul>
                <p>See <code>FRONTEND_README.md</code> for detailed implementation guide.</p>
              </div>
              <form>
                <div class="mb-3">
                  <label class="form-label">Email</label>
                  <input type="email" class="form-control" placeholder="Enter email" disabled>
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input type="password" class="form-control" placeholder="Enter password" disabled>
                </div>
                <button type="submit" class="btn btn-primary w-100" disabled>Login (Not Implemented)</button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {}
