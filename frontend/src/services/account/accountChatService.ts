import axios from "axios";
import {API_BASE_URL} from "@/config/api.config.ts";

const baseUrl = API_BASE_URL + "/api/chats";

const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  return {
    "Authorization": `Bearer ${token}`
  };
};

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export default {
  /**
   * Fetches paginated discussions created by a specific user
   *
   * @param userId - The ID of the user whose discussions to retrieve
   * @param page - Page number to retrieve
   * @param size - Number of items per page
   * @returns Paginated response containing user discussions
   * @throws HTTP error with status and message if request fails
   */
  async getChatsOfUser(userId: number, page: number = 0, size: number = 20): Promise<PaginatedResponse<any>> {
    try {
      const response = await axios.get(`${baseUrl}/user/${userId}`, {
        headers: getAuthHeaders(),
        params: { page, size }
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
