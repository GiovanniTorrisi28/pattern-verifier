package com.patternverifier.bridge.correct;

public class RemoteControl {

    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        device.turnOn();
    }

    public void volumeUp() {
        device.setVolume(10);
    }
}
