import { API_BASE_URL } from '@/config/api.config'
import type {
  ApiResponse,
  ConversationDTO,
  ConversationDisplayDTO,
  MessageDTO
} from '../../types'

export class DirectMessageService {
  private static baseUrl = `${API_BASE_URL}/api/directmessage`

  private static getAuthHeaders(): HeadersInit {
    const token = localStorage.getItem('token')

    return {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    }
  }

  private static async handleResponse<T>(response: Response): Promise<T> {
    const data: ApiResponse<T> = await response.json()
    if (!response.ok) {
      console.log(data.message)
      throw new Error(data.message || 'An error occurred')
    }
    return data.data
  }

  /**
   * Send a direct message
   */
  static async createDirectMessage(
    receiverId: number,
    content: string
  ): Promise<MessageDTO> {
    const params = new URLSearchParams({ receiverId: String(receiverId), content })
    const response = await fetch(`${this.baseUrl}?${params.toString()}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    })

    return this.handleResponse<MessageDTO>(response)
  }

  /**
   * Get all conversations for the logged-in user
   */
  static async getConversations(): Promise<ConversationDisplayDTO[]> {
    const response = await fetch(`${this.baseUrl}/all/conversations`, {
      headers: this.getAuthHeaders(),
    })

    return this.handleResponse<ConversationDisplayDTO[]>(response)
  }

  /**
   * Get a single conversation with messages
   */
  static async getConversationById(
    conversationId: number
  ): Promise<ConversationDTO> {
    const params = new URLSearchParams({ conversationId: String(conversationId) })
    const response = await fetch(`${this.baseUrl}?${params.toString()}`, {
      headers: this.getAuthHeaders(),
    })

    return this.handleResponse<ConversationDTO>(response)
  }

  /**
   * Get messages of a conversation
   */
  static async getMessages(conversationId: number): Promise<MessageDTO[]> {
    const params = new URLSearchParams({ conversationId: String(conversationId) })
    const response = await fetch(`${this.baseUrl}/messages?${params.toString()}`, {
      headers: this.getAuthHeaders(),
    })

    return this.handleResponse<MessageDTO[]>(response)
  }
}
