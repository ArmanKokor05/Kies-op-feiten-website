import { API_BASE_URL } from "@/config/api.config";

export interface Election {
  id: number;
  year: number;
}

interface ApiResponse<T> {
  message: string;
  data: T;
}

export class Election2Service {
  private baseUrl = `${API_BASE_URL}/api/election`;

  async getAll(): Promise<Election[]> {
    const response = await fetch(this.baseUrl, {
      method: "GET",
      headers: {
        "Accept": "application/json"
      }
    });

    if (!response.ok) {
      throw new Error("Failed to fetch elections");
    }

    const json: ApiResponse<Election[]> = await response.json();
    return json.data;
  }
}
