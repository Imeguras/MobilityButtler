package main

import (
	"encoding/json"
	"log"
	"net/http"

	"api/middlewares"
	"api/models"

	"github.com/gorilla/mux"
	"golang.org/x/net/websocket"
)

var ws *websocket.Conn

func main() {
	router := mux.NewRouter()

	wsURL := "ws://10.20.140.120:8002/butler_speak_subscriber"
	connectToWebSocket(wsURL)

	router.HandleFunc("/butler_speak_subscriber", verificationHandler).Methods(http.MethodGet)
	router.HandleFunc("/cse-mn/butler/speak", postSpeak(ws)).Methods(http.MethodPost)

	enhancedRouter := middlewares.EnableCORS(middlewares.JSONContentTypeMiddleware(router))

	log.Fatal(http.ListenAndServe(":8001", enhancedRouter))
}

func connectToWebSocket(wsURL string) {
	var err error
	ws, err = websocket.Dial(wsURL, "", "http://localhost/")
	if err != nil {
		log.Fatalf("Failed to connect to WebSocket server: %v", err)
	}
}

func verificationHandler(w http.ResponseWriter, r *http.Request) {
	log.Printf("Received verification request: %v", r.URL)
	w.Header().Set("X-M2M-Origin", "CAdminButler")
	w.Header().Set("X-M2M-RI", "123")
	w.Header().Set("X-M2M-RVI", "3")
	w.WriteHeader(http.StatusOK)
	log.Printf("Verification request responded with status code: %d", http.StatusOK)
}

func postSpeak(ws *websocket.Conn) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var m2m models.M2MCin
		if err := json.NewDecoder(r.Body).Decode(&m2m); err != nil {
			http.Error(w, "Invalid request payload", http.StatusBadRequest)
			return
		}

		// Forward the subscribed data to the WebSocket server
		message, err := json.Marshal(m2m)
		if err != nil {
			http.Error(w, "Failed to encode JSON", http.StatusInternalServerError)
			return
		}

		if _, err := ws.Write(message); err != nil {
			log.Printf("Failed to send message: %v", err)
			http.Error(w, "Failed to send message", http.StatusInternalServerError)
			return
		}

		w.WriteHeader(http.StatusOK)
	}
}
