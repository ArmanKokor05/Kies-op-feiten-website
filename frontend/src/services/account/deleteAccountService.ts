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
   * Permanently deletes a user account
   *
   * @param userId - The ID of the user account to delete
   * @returns Resolves when account is successfully deleted
   * @throws HTTP error with status and message if deletion fails
   *
   * @remarks
   * This action is irreversible and will permanently remove all user data from chosen id
   */
  async deleteAccount(userId: number): Promise<void> {
    try {
      const response = await axios.delete(`${baseUrl}/${userId}`, {
        headers: getAuthHeaders()
      });

      return response.data;
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
