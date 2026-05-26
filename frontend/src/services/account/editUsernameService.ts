import axios from "axios";
import {API_BASE_URL} from "@/config/api.config.ts";

const baseUrl = API_BASE_URL + "/api/users";

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };
};
export default {
  /**
   * Changes the username for a user account
   *
   * @param userId - The ID of the user whose name to change
   * @param newName - The new username to set
   * @returns New JWT token with updated user information
   * @throws HTTP error with status and message if update fails
   */
  async changeName(userId: number, newName: string): Promise<string> {
    try {
      const response = await axios.put(`${baseUrl}/${userId}/username`,
        { name: newName }, {
          headers: getAuthHeaders()
        });

      const token = response.data.data;
      localStorage.setItem('token', token);
      return token;
    }
    catch (error: any) {
      if (axios.isAxiosError(error) && error.response) {
        const status = error.response.status;
        const message = error.response.data?.message || error.response.statusText;
        throw new Error(`HTTP ${status}: ${message}`);
      }
      throw error;
    }
  },
};
