import axios from 'axios';
import { API_BASE_URL } from '@/config/api.config';

const XML_API_URL = `${API_BASE_URL}/api/xml`;
const PARTY_API_BASE_URL = `${API_BASE_URL}/api/party-results`;

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export interface CandidateResult {
  id: number;
  candidateId: string;
  validVotes: number;
}

export interface PartyResult {
  id: number;
  partyId: string;
  partyName: string;
  validVotes: number;
  seats: number;
  candidateResults: CandidateResult[];
}

export interface PartyResultDTO {
  id: number;
  partyId: string;
  partyName: string;
  validVotes: number;
  seats: number;
  region: string;
  regionId: string;
  electionYear: number;
}

export interface Election {
  id: number;
  electionId: string;
  electionName: string;
  electionCategory: string;
  electionDate: string;
  region: string;
  regionId: string;
  municipality: string;
  municipalityId: string;
  totalCounted: number;
  cast: number;
  partyResults: PartyResult[];
}

export const partyResultService = {

  getSeatsByYear(year: number) {
    return axios.get<PartyResult[]>(`${PARTY_API_BASE_URL}/seats/${year}`, {
      headers: getAuthHeader()
    });
  },

  getSeatsByYearAndRegion(year: number, region?: string) {
    const params = region ? { region } : {};
    return axios.get<PartyResultDTO[]>(`${PARTY_API_BASE_URL}/seats/${year}/filter`, {
      params,
      headers: getAuthHeader()
    });
  },

  getSeatsByYearAndMunicipality(year: number, municipality?: string) {
    const params = municipality ? { municipality } : {};
    return axios.get<PartyResultDTO[]>(`${PARTY_API_BASE_URL}/seats/${year}/municipalities`, {
      params,
      headers: getAuthHeader()
    });
  },

  getAvailableYears() {
    return axios.get<number[]>(`${PARTY_API_BASE_URL}/years`, {
      headers: getAuthHeader()
    });
  },

  getAvailableProvinces() {
    return axios.get<string[]>(`${PARTY_API_BASE_URL}/provinces`, {
      headers: getAuthHeader()
    });
  },

  getAvailableMunicipalities() {
    return axios.get<string[]>(`${PARTY_API_BASE_URL}/municipalities`, {
      headers: getAuthHeader()
    });
  },

  getAvailableKieskringen() {
    return axios.get<string[]>(`${PARTY_API_BASE_URL}/kieskringen`, {
      headers: getAuthHeader()
    });
  },

  getAvailableRegions() {
    return axios.get<string[]>(`${PARTY_API_BASE_URL}/regions`, {
      headers: getAuthHeader()
    });
  }
};

export default {
  getAllElections() {
    return axios.get<Election[]>(XML_API_URL, {
      headers: getAuthHeader()
    });
  },

  getElectionById(id: number) {
    return axios.get<Election>(`${XML_API_URL}/${id}`, {
      headers: getAuthHeader()
    });
  },

  uploadXml(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return axios.post<Election>(XML_API_URL, formData, {
      headers: {
        ...getAuthHeader(),
        'Content-Type': 'multipart/form-data'
      }
    });
  },

  uploadMultipleXml(files: File[]) {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    return axios.post<Election[]>(`${XML_API_URL}/batch`, formData, {
      headers: {
        ...getAuthHeader(),
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};