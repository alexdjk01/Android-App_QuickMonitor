package ro.ase.dma.connectinfluxdb;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Engine implements Parcelable {

    String temperatureTime;     //T
    String temperatureValue ;    //T
    String powerTime;           //P
    String powerValue;          //P
    String powerFactorTime;     //PF
    String powerFactorValue;    //PF
    String tensionTime;        //V
    String tensionValue;       //V
    String amperageTime;       //I
    String amperageValue;      //I

    public Engine(String temperatureTime, String temperatureValue, String powerTime, String powerValue, String powerFactorTime, String powerFactorValue, String tensionTime, String tensionValue, String amperageTime, String amperageValue) {
        this.temperatureTime = temperatureTime;
        this.temperatureValue = temperatureValue;
        this.powerTime = powerTime;
        this.powerValue = powerValue;
        this.powerFactorTime = powerFactorTime;
        this.powerFactorValue = powerFactorValue;
        this.tensionTime = tensionTime;
        this.tensionValue = tensionValue;
        this.amperageTime = amperageTime;
        this.amperageValue = amperageValue;
    }

    //copy construcotr
    public Engine(Engine other) {
        this.temperatureTime = other.temperatureTime;
        this.temperatureValue = other.temperatureValue;
        this.powerTime = other.powerTime;
        this.powerValue = other.powerValue;
        this.powerFactorTime = other.powerFactorTime;
        this.powerFactorValue = other.powerFactorValue;
        this.tensionTime = other.tensionTime;
        this.tensionValue = other.tensionValue;
        this.amperageTime = other.amperageTime;
        this.amperageValue = other.amperageValue;
    }

    @Override
    public String toString() {
        return "Engine{" +
                "temperatureTime='" + temperatureTime + '\'' +
                ", temperatureValue='" + temperatureValue + '\'' +
                ", powerTime='" + powerTime + '\'' +
                ", powerValue='" + powerValue + '\'' +
                ", powerFactorTime='" + powerFactorTime + '\'' +
                ", powerFactorValue='" + powerFactorValue + '\'' +
                ", tensionTime='" + tensionTime + '\'' +
                ", tensionValue='" + tensionValue + '\'' +
                ", amperageTime='" + amperageTime + '\'' +
                ", amperageValue='" + amperageValue + '\'' +
                '}';
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Engine engine = (Engine) o;
        return Objects.equals(temperatureTime, engine.temperatureTime) && Objects.equals(temperatureValue, engine.temperatureValue) && Objects.equals(powerTime, engine.powerTime) && Objects.equals(powerValue, engine.powerValue) && Objects.equals(powerFactorTime, engine.powerFactorTime) && Objects.equals(powerFactorValue, engine.powerFactorValue) && Objects.equals(tensionTime, engine.tensionTime) && Objects.equals(tensionValue, engine.tensionValue) && Objects.equals(amperageTime, engine.amperageTime) && Objects.equals(amperageValue, engine.amperageValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperatureTime, temperatureValue, powerTime, powerValue, powerFactorTime, powerFactorValue, tensionTime, tensionValue, amperageTime, amperageValue);
    }

    public String getTemperatureTime() {
        return temperatureTime;
    }

    public void setTemperatureTime(String temperatureTime) {
        this.temperatureTime = temperatureTime;
    }

    public String getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(String temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public String getPowerTime() {
        return powerTime;
    }

    public void setPowerTime(String powerTime) {
        this.powerTime = powerTime;
    }

    public String getPowerValue() {
        return powerValue;
    }

    public void setPowerValue(String powerValue) {
        this.powerValue = powerValue;
    }

    public String getPowerFactorTime() {
        return powerFactorTime;
    }

    public void setPowerFactorTime(String powerFactorTime) {
        this.powerFactorTime = powerFactorTime;
    }

    public String getPowerFactorValue() {
        return powerFactorValue;
    }

    public void setPowerFactorValue(String powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    public String getTensionTime() {
        return tensionTime;
    }

    public void setTensionTime(String tensionTime) {
        this.tensionTime = tensionTime;
    }

    public String getTensionValue() {
        return tensionValue;
    }

    public void setTensionValue(String tensionValue) {
        this.tensionValue = tensionValue;
    }

    public String getAmperageTime() {
        return amperageTime;
    }

    public void setAmperageTime(String amperageTime) {
        this.amperageTime = amperageTime;
    }

    public String getAmperageValue() {
        return amperageValue;
    }

    public void setAmperageValue(String amperageValue) {
        this.amperageValue = amperageValue;
    }

    protected Engine(Parcel in) {
        temperatureTime = in.readString();
        temperatureValue = in.readString();
        powerTime = in.readString();
        powerValue = in.readString();
        powerFactorTime = in.readString();
        powerFactorValue = in.readString();
        tensionTime = in.readString();
        tensionValue = in.readString();
        amperageTime = in.readString();
        amperageValue = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temperatureTime);
        dest.writeString(temperatureValue);
        dest.writeString(powerTime);
        dest.writeString(powerValue);
        dest.writeString(powerFactorTime);
        dest.writeString(powerFactorValue);
        dest.writeString(tensionTime);
        dest.writeString(tensionValue);
        dest.writeString(amperageTime);
        dest.writeString(amperageValue);
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
}
