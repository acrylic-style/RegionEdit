package xyz.acrylicstyle.region.internal.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NopeReentrantLock extends ReentrantLock {
    @Override
    public void lock() {}

    @Override
    public void unlock() {}

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) { return true; }

    @Override
    public boolean tryLock() { return true; }

    @Override
    public boolean isLocked() { return false; }

    @Override
    public boolean hasWaiters(Condition condition) { return false; }

    @Override
    public boolean isHeldByCurrentThread() { return true; }

    @Override
    public int getHoldCount() { return 0; }
    
    @Override
    public void lockInterruptibly() {}
}
