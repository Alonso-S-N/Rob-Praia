import asyncio
import json
import time
from networktables import NetworkTables
import websockets

ROBORIO_IP = "10.91.63.2"
WS_PORT = 5810

def connect_nt():
    print("Conectando NT...")
    NetworkTables.initialize(server=ROBORIO_IP)
    while not NetworkTables.isConnected():
        time.sleep(0.1)
    print("✅ NT conectado")

async def handle_ws(ws):
    phone = NetworkTables.getTable("phone")
    print("🟢 Browser conectado")

    async for msg in ws:
        data = json.loads(msg)
        if data["action"] == "put":
            key = data["key"]
            val = data["value"]

            if isinstance(val, bool):
                phone.putBoolean(key, val)
            elif isinstance(val, (int, float)):
                phone.putNumber(key, val)
            else:
                phone.putString(key, str(val))

async def main():
    connect_nt()
    server = await websockets.serve(handle_ws, "0.0.0.0", WS_PORT)
    print(f"WS rodando em ws://localhost:{WS_PORT}")
    await server.wait_closed()

if __name__ == "__main__":
    asyncio.run(main())
