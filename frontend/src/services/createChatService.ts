import axios from "axios";
import { API_BASE_URL } from "@/config/api.config";

export default {
  async createChat(discussionId: number, content: string) {
    try {
      const token = localStorage.getItem("token");

      const response = await axios.post(
        `${API_BASE_URL}/api/chats/create`,
        {
          discussion_id: discussionId,
          content: content
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
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
