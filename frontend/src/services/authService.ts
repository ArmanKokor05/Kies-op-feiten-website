import axios from 'axios';
import { API_BASE_URL } from '@/config/api.config';

const AUTH_API_URL = `${API_BASE_URL}/auth`;

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

const authService = {
  async login(email: string, password: string) {
    const response = await fetch(`${AUTH_API_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email, password }).toString(),
    });

    if (!response.ok) {
      const errorBody = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorBody}`);
    }

    const token = await response.text();
    localStorage.setItem('token', token);
    return token;
  },

  async register(userData: RegisterRequest): Promise<string> {
    const response = await axios.post<string>(`${AUTH_API_URL}/register`, userData);
    const token = response.data;
    localStorage.setItem('token', token);
    return token;
  },

  async deleteAccount(userId: number): Promise<void> {
    try {
      const response = await axios.delete(`${AUTH_API_URL}/deleteUserAccount/${userId}`, {
        headers: {
          "Content-Type": "application/json",
        },
      });

      return response.data;
    }
    catch (error: any) {
      if (error.response) {
        const status = error.response.status;
        const message = error.response.data || error.response.statusText;
        throw new Error(`HTTP ${status}: ${message}`);
      }
    }
  },

  async changeName(userId: number, newName: string): Promise<string> {
    try {
      const response = await axios.put(`${AUTH_API_URL}/changeUsername/${userId}`,
          { name: newName }, {
            headers: { "Content-Type": "application/json"
            },
          });

      const token = response.data;
      localStorage.setItem('token', token);
      return token;
    }
    catch (error: any) {
      if (error.response) {
        const status = error.response.status;
        const message = error.response.data || error.response.statusText;
        throw new Error(`HTTP ${status}: ${message}`);
      }
      throw error;
    }
  },

  logout() {
    localStorage.removeItem('token');
  },

  isAuthenticated(): boolean {
    return localStorage.getItem('token') !== null;
  },

  getToken(): string | null {
    return localStorage.getItem('token');
  }
};

export default authService;
