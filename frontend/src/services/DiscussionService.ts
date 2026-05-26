import {API_BASE_URL} from "@/config/api.config";
import axios from "axios";
import type {ApiResponse, Discussion} from "../../types.ts";

export class DiscussionService {
  private baseUrl: string = `${API_BASE_URL}/api/discussions`;

  public async createDiscussion(title: string, content: string) {
    const token = localStorage.getItem("token");

    const response = await fetch(`${this.baseUrl}/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ title, content }),
    });

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      const errorMessage = data?.message || response.statusText;
      throw new Error(errorMessage);
    }

    return data;
  }

  public async getDiscussionById(id: number) {
    const token = localStorage.getItem("token");
    const response = await axios.get<ApiResponse<Discussion>>(
      `${this.baseUrl}/discussions/${id}`,
      { headers: { 'Authorization': `Bearer ${token}` } }
    );
    return response.data.data;
  }
}
