package com.exomatik.zcodex.services.timer;

public interface TListener {
    String updateDataOnTick(long remainingTimeInMs);
    void onTimerFinished();
}
