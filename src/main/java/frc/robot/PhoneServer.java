package frc.robot;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class PhoneServer extends WebSocketServer {

  private final NetworkTable phoneTable;

  public PhoneServer() {
    super(new InetSocketAddress(5805));
    phoneTable = NetworkTableInstance.getDefault().getTable("phone");
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    System.out.println("📱 Phone conectado: " + conn.getRemoteSocketAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    System.out.println("📴 Phone desconectado");
    phoneTable.getEntry("stop").setBoolean(true);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    // formato: key:value
    if (!message.contains(":")) return;

    String[] p = message.split(":", 2);
    String key = p[0];
    String raw = p[1];

    // boolean
    if (raw.equals("true") || raw.equals("false")) {
      phoneTable.getEntry(key).setBoolean(Boolean.parseBoolean(raw));
      return;
    }

    // number
    try {
      double v = Double.parseDouble(raw);
      phoneTable.getEntry(key).setDouble(v);
    } catch (Exception e) {
      // ignora
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    System.out.println("❌ WS erro: " + ex.getMessage());
  }

  @Override
  public void onStart() {
    System.out.println("🚀 PhoneServer iniciado na porta 5805");
  }
}
