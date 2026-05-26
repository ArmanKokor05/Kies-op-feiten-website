import axios from "axios";
import type {ApiResponse, Party} from "../../types";
import { API_BASE_URL } from '@/config/api.config'

export class PartyService {
  private baseURL = API_BASE_URL+ "/api/party";

  async getAll(): Promise<Party[]> {
    const response = await axios.get<ApiResponse<Party[]>>(this.baseURL);
    return response.data.data;
  }
}
