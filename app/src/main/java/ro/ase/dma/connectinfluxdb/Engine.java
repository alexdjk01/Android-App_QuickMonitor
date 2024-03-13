package ro.ase.dma.connectinfluxdb;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Engine implements Parcelable {

    //double no String
    String temperatureTime;     //T
    Double temperatureValue ;    //T
    String powerTime;           //P
    Double powerValue;          //P
    String powerFactorTime;     //PF
    Double powerFactorValue;    //PF
    String tensionTime;        //V
    Double tensionValue;       //V
    String amperageTime;       //I
    Double amperageValue;      //I

    public Engine(String temperatureTime, Double temperatureValue, String powerTime, Double powerValue, String powerFactorTime, Double powerFactorValue, String tensionTime, Double tensionValue, String amperageTime, Double amperageValue) {
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

    public Double getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(Double temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public String getPowerTime() {
        return powerTime;
    }

    public void setPowerTime(String powerTime) {
        this.powerTime = powerTime;
    }

    public Double getPowerValue() {
        return powerValue;
    }

    public void setPowerValue(Double powerValue) {
        this.powerValue = powerValue;
    }

    public String getPowerFactorTime() {
        return powerFactorTime;
    }

    public void setPowerFactorTime(String powerFactorTime) {
        this.powerFactorTime = powerFactorTime;
    }

    public Double getPowerFactorValue() {
        return powerFactorValue;
    }

    public void setPowerFactorValue(Double powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    public String getTensionTime() {
        return tensionTime;
    }

    public void setTensionTime(String tensionTime) {
        this.tensionTime = tensionTime;
    }

    public Double getTensionValue() {
        return tensionValue;
    }

    public void setTensionValue(Double tensionValue) {
        this.tensionValue = tensionValue;
    }

    public String getAmperageTime() {
        return amperageTime;
    }

    public void setAmperageTime(String amperageTime) {
        this.amperageTime = amperageTime;
    }

    public Double getAmperageValue() {
        return amperageValue;
    }

    public void setAmperageValue(Double amperageValue) {
        this.amperageValue = amperageValue;
    }

    protected Engine(Parcel in) {
        temperatureTime = in.readString();
        temperatureValue = in.readDouble();
        powerTime = in.readString();
        powerValue = in.readDouble();
        powerFactorTime = in.readString();
        powerFactorValue = in.readDouble();
        tensionTime = in.readString();
        tensionValue = in.readDouble();
        amperageTime = in.readString();
        amperageValue = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temperatureTime);
        dest.writeDouble(temperatureValue);
        dest.writeString(powerTime);
        dest.writeDouble(powerValue);
        dest.writeString(powerFactorTime);
        dest.writeDouble(powerFactorValue);
        dest.writeString(tensionTime);
        dest.writeDouble(tensionValue);
        dest.writeString(amperageTime);
        dest.writeDouble(amperageValue);
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
