import axios from "axios";
import {API_BASE_URL} from "@/config/api.config.ts";

const baseUrl = API_BASE_URL + "/api/users";

const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  return {
    "Authorization": `Bearer ${token}`
  };
};

interface UserInfo {
  id: number;
  name: string;
  createdAt: string;
}

export default {
  /**
   * Fetches user information including username and creation date
   *
   * @param userId - The ID of the user to retrieve
   * @returns User information object
   * @throws HTTP error with status and message if request fails
   */
  async getUserInfo(userId: number): Promise<UserInfo> {
    try {
      const response = await axios.get(`${baseUrl}/${userId}`, {
        headers: getAuthHeaders()
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
