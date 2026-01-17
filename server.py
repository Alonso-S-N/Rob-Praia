#!/usr/bin/env python3
import asyncio
import json
import time
import signal
import sys
from networktables import NetworkTables
import websockets

ROBORIO_IP = "10.91.63.2"   # IP do RoboRIO
WS_HOST    = "0.0.0.0"      # escuta em todas as interfaces
WS_PORT    = 8766           # porta do WebSocket

# ---------- NetworkTables ----------
def connect_nt():
    print("Conectando NT →", ROBORIO_IP)
    NetworkTables.initialize(server=ROBORIO_IP)
    for _ in range(50):
        if NetworkTables.isConnected():
            print("✅ NT conectado")
            # teste rápido
            test_table = NetworkTables.getTable("phone")
            test_table.putNumber("testeEscrita", 3.14)
            print("Escrito 3.14 em phone/testeEscrita")
            return
        time.sleep(0.1)
    print("⚠️ NT NÃO conectado – continuo tentando…")

# ---------- WebSocket ----------
async def handle(ws):                      # <<<< SÓ 1 argumento
    peer = ws.remote_address
    print(f"🟢 Browser conectado: {peer}")
    try:
        async for msg in ws:
            if isinstance(msg, bytes):
                continue
            try:
                obj = json.loads(msg)
                if obj.get("action") != "put":
                    continue
                table = NetworkTables.getTable(obj["table"])
                key, val = obj["key"], obj["value"]
                if isinstance(val, bool):
                    table.putBoolean(key, val)
                elif isinstance(val, (int, float)):
                    table.putNumber(key, val)
                else:
                    table.putString(key, str(val))
                print(f"📡 PUT {obj['table']}/{key} = {val}")
            except json.JSONDecodeError:
                print("🚃 Mensagem inválida:", msg)
    except websockets.exceptions.ConnectionClosed:
        pass
    finally:
        print(f"🔴 Browser desconectado: {peer}")
# ---------- Graceful shutdown ----------
def shutdown(sig, frame):
    print("\n🛑 Encerrando servidor…")
    sys.exit(0)

signal.signal(signal.SIGINT, shutdown)

# ---------- Main ----------
async def main():
    connect_nt()
    async with websockets.serve(handle, WS_HOST, WS_PORT):
        print(f"🌐 WebSocket rodando em ws://{WS_HOST}:{WS_PORT}")
        await asyncio.get_running_loop().create_future()

if __name__ == "__main__":
    asyncio.run(main())