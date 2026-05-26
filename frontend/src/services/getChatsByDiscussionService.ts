import axios from "axios";
import { API_BASE_URL } from "@/config/api.config";

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export default {
  async getChatsByDiscussion(discussionId: number, page: number = 0, size: number = 5): Promise<PaginatedResponse<any>> {
    try {
      const response = await axios.get<PaginatedResponse<any>>(
        `${API_BASE_URL}/api/chats/discussion/${discussionId}`,
        {
          params: { page, size },
        }
      );

      return response.data;
    } catch (error: any) {
      if (axios.isAxiosError(error) && error.response) {
        const status = error.response.status;
        const message =
          error.response.data?.message || error.response.statusText;

        throw new Error(`HTTP ${status}: ${message}`);
      }
      throw error;
    }
  }
};
