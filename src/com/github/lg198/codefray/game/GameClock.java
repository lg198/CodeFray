package com.github.lg198.codefray.game;

public class GameClock {

    private ClockThread thread = new ClockThread();
    private Runnable runnable;

    public GameClock(Runnable r) {
        runnable = r;
    }

    public void setDelay(int d) {
        thread.delay = d;
    }

    public void stop() {
        thread.running = false;
    }

    public void start() {
        thread.running = true;
        thread.setDaemon(true);
        thread.start();
    }

    public void pause() {
        thread.paused = true;
    }

    public void unpause() {
        thread.paused = false;
        synchronized (thread.pauseLock) {
            thread.pauseLock.notify();
        }
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
