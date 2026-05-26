import type {ApiResponse, Candidate} from "../../types.ts";
import {API_BASE_URL} from "@/config/api.config.ts";
import axios from "axios";

export interface PaginatedCandidates {
  content: Candidate[]
  totalPages: number
  totalElements: number
  number: number
  size: number
  first: boolean
  last: boolean
}

export class CandidateService {
  private baseUrl = `${API_BASE_URL}/candidates`

  async getByElectionAndPartyPaginated(
    electionId: number,
    partyId: number,
    page = 0,
    size = 10
  ): Promise<PaginatedCandidates> {
    const response = await axios.get<ApiResponse<PaginatedCandidates>>(
      `${this.baseUrl}`,
      { params: { electionId, partyId, page, size } }
    )
    return response.data.data
  }
}
