package ro.ase.dma.connectinfluxdb;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Engine implements Parcelable {

    private int id;
    private String name;
    private Boolean isActive;
    private Double temperature; //T0 T1 T2
    private Double power; // P_testem3_0 P_testem3_1 P_testem3_2
    private Double powerFactor; // PF_0 PF_1 PF_2
    private Double tension; // V_0 V_1 V_2
    private Double amperage; // I_0 I_1 I_2

    public Engine(int id, String name, Boolean isActive, Double temperature, Double power, Double powerFactor, Double tension, Double amperage) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.temperature = temperature;
        this.power = power;
        this.powerFactor = powerFactor;
        this.tension = tension;
        this.amperage = amperage;
    }

    protected Engine(Parcel in) {
        id = in.readInt();
        name = in.readString();
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        if (in.readByte() == 0) {
            temperature = null;
        } else {
            temperature = in.readDouble();
        }
        if (in.readByte() == 0) {
            power = null;
        } else {
            power = in.readDouble();
        }
        if (in.readByte() == 0) {
            powerFactor = null;
        } else {
            powerFactor = in.readDouble();
        }
        if (in.readByte() == 0) {
            tension = null;
        } else {
            tension = in.readDouble();
        }
        if (in.readByte() == 0) {
            amperage = null;
        } else {
            amperage = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        if (temperature == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(temperature);
        }
        if (power == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(power);
        }
        if (powerFactor == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(powerFactor);
        }
        if (tension == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(tension);
        }
        if (amperage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amperage);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Engine> CREATOR = new Creator<Engine>() {
        @Override
        public Engine createFromParcel(Parcel in) {
            return new Engine(in);
        }

        @Override
        public Engine[] newArray(int size) {
            return new Engine[size];
        }
    };

    @Override
    public String toString() {
        return "Engine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", temperature=" + temperature +
                ", power=" + power +
                ", powerFactor=" + powerFactor +
                ", tension=" + tension +
                ", amperage=" + amperage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Engine engine = (Engine) o;
        return id == engine.id && Objects.equals(name, engine.name) && Objects.equals(isActive, engine.isActive) && Objects.equals(temperature, engine.temperature) && Objects.equals(power, engine.power) && Objects.equals(powerFactor, engine.powerFactor) && Objects.equals(tension, engine.tension) && Objects.equals(amperage, engine.amperage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isActive, temperature, power, powerFactor, tension, amperage);
    }
}
