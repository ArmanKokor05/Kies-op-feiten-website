import { API_BASE_URL } from "@/config/api.config";
import { Client } from "@stomp/stompjs";
import type { IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export class WebSocketService {
  private client: Client | null = null;

  private getJwtToken() {
    return localStorage.getItem("token");
  }

  connect(topics: string[], onMessage: (msg: IMessage, topic: string) => void) {
    if (!this.getJwtToken()) {
      console.error("No JWT token provided, cannot connect to WebSocket");
      return;
    }

    // Only create a new client if it doesn't exist
    if (!this.client) {
      this.client = new Client({
        webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
        connectHeaders: { Authorization: `Bearer ${this.getJwtToken()}` },
        debug: (str) => console.log("[STOMP]", str),
        reconnectDelay: 5000,
        heartbeatIncoming: 0,
        heartbeatOutgoing: 20000,
      });

      this.client.onConnect = () => {
        console.log("WebSocket connected");

        // Subscribe to topics
        topics.forEach((topic) => {
          const fullTopic = topic.startsWith("/") ? topic : `/${topic}`;
          console.log("Subscribing to:", fullTopic);

          this.client?.subscribe(fullTopic, (msg) => {
            try {
              onMessage(msg, fullTopic);
            } catch (err) {
              console.error("Error processing message", err);
            }
          });
        });
      };

      this.client.onStompError = (frame) => {
        console.error("Broker error:", frame.headers["message"]);
        console.error("Details:", frame.body);
      };

      this.client.activate();
    } else {
      // If client already exists, just subscribe to new topics
      topics.forEach((topic) => {
        const fullTopic = topic.startsWith("/") ? topic : `/${topic}`;
        console.log("Subscribing to:", fullTopic);

        this.client?.subscribe(fullTopic, (msg) => {
          try {
            onMessage(msg, fullTopic);
          } catch (err) {
            console.error("Error processing message", err);
          }
        });
      });
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }
}
