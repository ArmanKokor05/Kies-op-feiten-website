import axios from 'axios';
import { API_BASE_URL } from '@/config/api.config';
import type { SearchUserDTO, ApiResponse } from'../../types'

export class UserService {
  private static readonly BASE_URL = `${API_BASE_URL}/api/users`;

  static async searchUsers(name: string): Promise<SearchUserDTO[]> {
    if (!name || name.trim().length < 2) return [];

    const response = await axios.get<{ message: string; data: SearchUserDTO[] }>(
      `${this.BASE_URL}/search`,
      {
        params: { name },
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      }
    );

    return response.data.data;
  }

  static async getUserId(): Promise<number> {
    const response = await fetch(`${this.BASE_URL}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
      },
    });

    const data: ApiResponse<number> = await response.json().catch(() => null);

    if (!response.ok) {
      const errorMessage = data?.message || response.statusText;
      throw new Error(errorMessage);
    }

    return data.data;
  }
}
