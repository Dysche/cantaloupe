package org.cantaloupe.network.tcp.client.packets;

import org.cantaloupe.network.session.Session;
import org.cantaloupe.network.tcp.TCPClientPacket;

public class C001PacketDisconnect extends TCPClientPacket {
    private C001PacketDisconnect(Session session) {
        super(session);
    }

    public static C001PacketDisconnect of(Session session) {
        return new C001PacketDisconnect(session);
    }

    @Override
    public byte getID() {
        return 1;
    }
}