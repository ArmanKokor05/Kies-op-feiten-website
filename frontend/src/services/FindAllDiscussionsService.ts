import {API_BASE_URL} from "@/config/api.config.ts";
import axios from "axios";

const baseUrl = API_BASE_URL + "/api/discussions";

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export class FindAllDiscussionsService {
  public async getAllDiscussions(page: number = 0, size: number = 20): Promise<PaginatedResponse<any>> {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${baseUrl}/AllDiscussions`, {
        params: { page, size },
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error: any) {
      if (axios.isAxiosError(error) && error.response) {
        const status = error.response.status;
        const message = error.response.data?.message || error.response.statusText;
        throw new Error(`HTTP ${status}: ${message}`);
      }
      throw error;
    }
  }
}

export default new FindAllDiscussionsService();
