package me.eduardwayland.mooncraft.waylander.database.messenger;

import org.jetbrains.annotations.NotNull;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public final class RedisSubscription extends JedisPubSub implements Runnable {

    /*
    Fields
     */
    private final @NotNull RedisMessenger redisMessenger;

    /*
    Constructor
     */
    public RedisSubscription(@NotNull RedisMessenger redisMessenger) {
        this.redisMessenger = redisMessenger;
    }

    /*
    Override Methods
     */
    @Override
    public void run() {
        boolean wasBroken = false;
        while (!Thread.interrupted() && !redisMessenger.isClosed()) {
            try (Jedis jedis = redisMessenger.getJedisPool().getResource()) {
                if (wasBroken) {
                    System.out.printf("[%s] Redis pub/sub connection re-established.", redisMessenger.getIdentifier());
                    wasBroken = false;
                }
                jedis.subscribe(this, redisMessenger.getIncomingChannel());
            } catch (Exception e) {
                wasBroken = true;
                System.out.printf("[%s] Redis pub/sub connection dropped, trying to re-open the connection. Error: %s", redisMessenger.getIdentifier(), e.getMessage());
                try {
                    unsubscribe();
                } catch (Exception ignored) {
                }

                // Sleep for 5 seconds to prevent spam in console
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onMessage(@NotNull String channel, @NotNull String message) {
        if (redisMessenger.getConsumer() == null) return;
        if (redisMessenger.getIncomingChannel() == null) return;
        if (!channel.equals(redisMessenger.getIncomingChannel())) return;
        try {
            redisMessenger.getConsumer().consumeIncomingMessageAsString(message);
        } catch (Exception e) {
            System.out.printf("[%s] [Redis Pub/Sub] Error onMessage: %s", redisMessenger.getIdentifier(), e.getMessage());
        }
    }
}