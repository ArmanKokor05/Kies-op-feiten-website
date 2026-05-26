export interface InputField {
  key: string
  value: string
}

export interface TextArea {
  key: string
  value: string
  rows: number
}

export interface PollingStation {
  id: number
  name: string
  zipcode: string
  municipality: string
}

export interface PaginatedPollingStations {
  content: PollingStation[]
  totalPages: number
  totalElements: number
  number: number
  size: number
  first: boolean
  last: boolean
}

export interface Municipality {
  id: number
  name: string
};

export interface Party {
  id: number
  acronym: string
  name: string
}

export interface Candidate {
  id: number
  firstName: string
  lastName: string
  initials: string
  gender: string
  qualifyingAdress: string
}

export interface ApiResponse<T> {
  message: string;
  data: T;
}

export interface WebSocketMessage {
  userId: number;
  userName: string;
  content: string;
  createdAt: string;
}

export interface ActiveConversation {
  id: number;
  name: string;
}

export interface ConversationDisplayDTO {
  conversationId: number;
  userId: number;
  conversationName: string;
  lastMessage: string;
  unreadCount: number;
}

export interface ConversationDTO {
  conversationId: number;
  userId: number;
  conversationName: string;
  messages: MessageDTO[];
}

export interface MessageDTO {
  conversationId: number;
  senderId: number;
  receiverId: string;
  content: string;
  createdAt: string;
}

export interface SearchUserDTO {
  userId: number;
  name: string;
}

export interface Discussion {
  id: number;
  createdAt: string;
  title: string;
  content: string;
  upvotes: number;
  downvotes: number;
  userName: string;
}

export interface Chat {
  id: number;
  content: string;
  upvotes: number;
  downvotes: number;
  created_at: string;
  discussion_title: string;
  discussion_id: number;
  userName: string;
}
