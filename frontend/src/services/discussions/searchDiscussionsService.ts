import axios from "axios";
import {API_BASE_URL} from "@/config/api.config.ts";

const baseUrl = API_BASE_URL + "/api/discussions";


interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export default {
  /**
   * Fetches paginated discussions found with the matching title
   *
   * @param title - The title written by user to find the matching discussions
   * @param page - Page number to retrieve
   * @param size - Number of items per page
   * @returns Paginated response containing found discussions
   * @throws HTTP error with status and message if request fails
   */
  async searchDiscussions(title: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<any>> {
    try {
      const response = await axios.get(`${baseUrl}/search`, {
        params: { title, page, size }
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
};
