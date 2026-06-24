package net.md_5.bungee.scheduler;

import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxySchedulerException;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Data
public class BungeeTask implements Runnable, ScheduledTask {

    private final BungeeScheduler sched;
    private final int id;
    private final Plugin owner;
    private final Runnable task;
    //
    private final long delay;
    private final long period;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private AtomicReference<Timeout> pendingTimeout = new AtomicReference<Timeout>();

    public BungeeTask(BungeeScheduler sched, int id, Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
        this.sched = sched;
        this.id = id;
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
    }

    @Override
    public void cancel() {
        boolean wasRunning = running.getAndSet(false);

        if (wasRunning) {
            Timeout timeout = pendingTimeout.get();
            if (timeout != null) {
                timeout.cancel();
            }
            sched.cancel0(this);
        }
    }

    void scheduleWith(Timer timer) {
        if (!running.get()){
            return;
        }
        pendingTimeout.set(timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (!running.get()) {
                    return;
                }
                owner.getExecutorService().execute(BungeeTask.this);
            }
        }, delay, TimeUnit.MILLISECONDS));
    }

    private void reSchedule(Timer timer) {
        if (!running.get() || period <= 0) {
            cancel();
            return;
        }
        pendingTimeout.set(timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (!running.get()) {
                    return;
                }
                owner.getExecutorService().execute(BungeeTask.this);
            }
        }, period, TimeUnit.MILLISECONDS));
    }

    @Override
    public void run() {
        if (!running.get()) {
            return;
        }

        try {
            task.run();
        } catch (Exception t) {
            //Waterfall start - throw exception event
            String msg = String.format("Task %s encountered an exception", this);
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, msg, t);
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyExceptionEvent(new ProxySchedulerException(msg, t, this)));
            //Waterfall end
        }

        if (period <= 0) {
            cancel();
        } else {
            reSchedule(sched.getTimer());
        }
    }
}
