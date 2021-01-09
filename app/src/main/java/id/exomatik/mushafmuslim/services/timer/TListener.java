package id.exomatik.mushafmuslim.services.timer;

public interface TListener {
    String updateDataOnTick(long remainingTimeInMs);
    void onTimerFinished();
}
