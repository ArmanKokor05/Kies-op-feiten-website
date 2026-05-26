import { API_BASE_URL } from '@/config/api.config';

export interface QuestionResponseDTO {
  title: string
  question: string
  sourceUrl: string
  sourceName: string
}

export class Question {
  private baseUrl = `${API_BASE_URL}/questions`

  public async getQuestions(): Promise<QuestionResponseDTO[]> {
    const response = await fetch(this.baseUrl);

    if (!response.ok) {
      throw new Error(`Failed to fetch questions: ${response.status} ${response.statusText}`)
    }
//queen
    return await response.json()
  }
}
