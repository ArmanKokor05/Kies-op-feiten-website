import axios from "axios";
import type { Municipality, ApiResponse } from "../../types";
import { API_BASE_URL } from '@/config/api.config'

export class MunicipalityService {
  private baseURL = API_BASE_URL+ "/api/municipality";

  async getAll(): Promise<Municipality[]> {
    const response = await axios.get<ApiResponse<Municipality[]>>(this.baseURL);
    return response.data.data;
  }
}
