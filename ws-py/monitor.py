import requests
import socket
import json
import atexit
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer
from time import sleep
from zeroconf import IPVersion, ServiceInfo, Zeroconf
import asyncio
import websockets

# Constants
_portCSE = 8000
_portNoti = 3001
_portWebSocket = 8765  # WebSocket server port

# Global variables
_isEnabled = False
_host = None
_callback = None
_notificationURI = None
_name = None
_websocket_server = None

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(('8.8.8.8', 1))
    local_ip_address = s.getsockname()[0]
    s.close()
    return local_ip_address

def setup_notifications(callback=None, host='127.0.0.1', port=1400, flag=0):
    if not host:
        print("Error: Host cannot be empty.")
        return False
    if port == -1:
        print("Error: Port cannot be -1.")
        return False

    global _host, _portNoti, _callback, _notificationURI
    _host = host
    _portNoti = port
    _callback = callback
    _notificationURI = f'http://{_host}:{_portNoti}'
    _start_notification_server()
    enable_notifications()
    print(f"Notification server running on port {_portNoti}")
    return True

def enable_notifications():
    global _isEnabled
    if _isEnabled:
        return
    _isEnabled = True

def disable_notifications():
    global _isEnabled
    if not _isEnabled:
        return
    _isEnabled = False

@atexit.register
def shutdown_notifications():
    global _notificationURI
    if not _notificationURI:
        return
    disable_notifications()
    _notificationURI = None
    _stop_notification_server()

def is_notification_enabled():
    return _isEnabled

def get_notification_uri():
    return _notificationURI

_server = None
_thread = None

def _start_notification_server():
    global _server, _thread
    if _thread:
        return
    _server = HTTPNotificationServer(('', _portNoti), HTTPNotificationHandler)
    _thread = threading.Thread(target=_server.run)
    _thread.start()

def _stop_notification_server():
    global _server, _thread
    if not _server or not _thread:
        return
    _server.shutdown()
    _thread.join()
    _server = None
    _thread = None

class HTTPNotificationServer(HTTPServer):
    def run(self):
        try:
            self.serve_forever()
        finally:
            self.server_close()

def get_all_sub_elements_json(jsn, name):
    result = []
    for elem_name, elem in jsn.items():
        if elem_name == name:
            result.append(elem)
        elif isinstance(elem, dict):
            result.extend(get_all_sub_elements_json(elem, name))
        elif isinstance(elem, list):
            for e in elem:
                if isinstance(e, dict):
                    result.extend(get_all_sub_elements_json(e, name))
    return result

class HTTPNotificationHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        self.send_response(200)
        self.send_header('X-M2M-RSC', '2000')
        ri = self.headers['X-M2M-RI']
        self.send_header('X-M2M-RI', ri)
        self.end_headers()

        length = int(self.headers['Content-Length'])
        content_type = self.headers['Content-Type']
        post_data = self.rfile.read(length)
        if _isEnabled:
            threading.Thread(target=self._handle_json, args=(post_data,)).start()

    def log_message(self, format, *args):
        return

    def _handle_json(self, data):
        jsn = json.loads(data.decode('utf-8'))
        print(jsn)
        # Send JSON data to WebSocket clients
        asyncio.run_coroutine_threadsafe(_websocket_server.send_message_to_all_clients(jsn), _websocket_server.loop)

class WebSocketServer:
    def __init__(self, port):
        self.port = port
        self.server = None
        self.clients = set()
        self.loop = asyncio.new_event_loop()

    async def handle_connection(self, websocket, path):
        print("WebSocket connection established!")
        self.clients.add(websocket)
        try:
            while True:
                message = await websocket.recv()
                print(f"Received message: {message}")
                # Handle the message here
                await websocket.send(f"Echo: {message}")
        except websockets.ConnectionClosed:
            print("WebSocket connection closed")
        finally:
            self.clients.remove(websocket)

    async def send_message_to_all_clients(self, message):
        if self.clients:
            await asyncio.wait([client.send(json.dumps(message)) for client in self.clients])

    async def start(self):
        self.server = await websockets.serve(self.handle_connection, "localhost", self.port)
        print(f"WebSocket server started on port {self.port}")
        await self.server.wait_closed()

    def stop(self):
        self.server.ws_server.close()
        asyncio.run_coroutine_threadsafe(self.server.ws_server.wait_closed(), self.loop).result()

def start_websocket_server():
    global _websocket_server
    _websocket_server = WebSocketServer(_portWebSocket)
    asyncio.set_event_loop(_websocket_server.loop)
    _websocket_server.loop.run_until_complete(_websocket_server.start())

if __name__ == '__main__':
    local_ip = get_ip()
    setup_notifications(None, local_ip, _portNoti, 0)

    # Start WebSocket server in main thread
    threading.Thread(target=start_websocket_server).start()

    try:
        while True:
            sleep(0.1)
    except KeyboardInterrupt:
        pass
    finally:
        print("Unregistering...")
        zeroconf.unregister_service(info)
        zeroconf.close()
        shutdown_notifications()
        _websocket_server.stop()
