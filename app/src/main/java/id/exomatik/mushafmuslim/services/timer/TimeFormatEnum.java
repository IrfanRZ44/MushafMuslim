package id.exomatik.mushafmuslim.services.timer;

public enum TimeFormatEnum {
    MILLIS,
    SECONDS,
    MINUTES,
    HOUR,
    DAY;

    public String canonicalForm() {
        return this.name();
    }

    public static TimeFormatEnum fromCanonicalForm(String canonical) {
        return valueOf(TimeFormatEnum.class, canonical);
    }
}
