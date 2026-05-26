import axios from "axios"
import { API_BASE_URL } from '@/config/api.config'
import type { ApiResponse, PaginatedPollingStations } from '../../types'

export class PollingStationService {
  private baseUrl = `${API_BASE_URL}/api/pollingstation`

  async getByElectionIdPaginated(
    electionId: number,
    page = 0,
    size = 10
  ): Promise<PaginatedPollingStations> {
    const response = await axios.get<ApiResponse<PaginatedPollingStations>>(
      `${this.baseUrl}/election`,
      {
        params: {
          electionId,
          page,
          size
        }
      }
    )

    return response.data.data
  }

  async getByElectionAndMunicipalityPaginated(
    electionId: number,
    municipalityId: number,
    page = 0,
    size = 10
  ): Promise<PaginatedPollingStations> {
    const response = await axios.get<ApiResponse<PaginatedPollingStations>>(
      `${this.baseUrl}/municipality`,
      { params: { electionId, municipalityId, page, size } }
    )
    return response.data.data
  }
}
