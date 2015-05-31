package com.github.lg198.codefray.game;

public class GameClock {

    private ClockThread thread = new ClockThread();
    private Runnable runnable;

    private long startTime = 0, currentTime = 0;

    public GameClock(Runnable r) {
        runnable = r;
    }

    public void setDelay(int d) {
        thread.delay = d;
    }

    public long stop() {
        thread.running = false;
        currentTime += System.currentTimeMillis() - startTime;
        return currentTime;
    }

    public void start() {
        thread.running = true;
        thread.setDaemon(true);
        thread.start();
        startTime = System.currentTimeMillis();
    }

    public void pause() {
        currentTime += System.currentTimeMillis() - startTime;
        thread.paused = true;
    }

    public void unpause() {
        thread.paused = false;
        synchronized (thread.pauseLock) {
            thread.pauseLock.notify();
        }
        startTime = System.currentTimeMillis();
    }

    private class ClockThread extends Thread {

        volatile int delay = 1000;
        volatile boolean running = true;
        volatile boolean paused = false;
        volatile Object pauseLock = new Object();

        @Override
        public void run() {
            try {
                while (running) {
                    if (paused) {
                        synchronized(pauseLock) {
                            pauseLock.wait();
                        }
                    }
                    runnable.run();
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
