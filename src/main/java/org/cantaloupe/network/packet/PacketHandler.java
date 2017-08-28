package org.cantaloupe.network.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.cantaloupe.data.DataContainer;
import org.cantaloupe.network.IConnection;
import org.cantaloupe.network.session.Session;
import org.cantaloupe.network.tcp.TCPPacket;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class PacketHandler {
    private final List<IPacketListener>                         listeners;
    private final DataContainer<Byte, Class<? extends IPacket>> packetClasses;

    public PacketHandler() {
        this.listeners = new ArrayList<IPacketListener>();
        this.packetClasses = DataContainer.of();
    }

    public void registerPacketClass(byte packetID, Class<? extends IPacket> packetClass) {
        this.packetClasses.put(packetID, packetClass);
    }

    public void registerListener(IPacketListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(IPacketListener listener) {
        this.listeners.remove(listener);
    }

    public void sendPacket(IConnection connection, IPacket packet) {
        connection.sendPacket(packet);
    }

    public void handlePacket(IConnection connection, byte[] bytes) {
        ByteArrayDataInput data = ByteStreams.newDataInput(bytes);
        byte packetID = data.readByte();
        
        if (this.packetClasses.containsKey(packetID)) {
            TCPPacket packet = null;

            try {
                Constructor<?> constructor = this.packetClasses.get(packetID).getDeclaredConstructor(Session.class);
                constructor.setAccessible(true);

                packet = (TCPPacket) constructor.newInstance(new Object[] {
                        null
                });

                packet.read(ByteStreams.newDataInput(bytes, 1));

                for (IPacketListener listener : this.listeners) {
                    listener.onPacketRecieved(connection, packet);
                }
            } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}