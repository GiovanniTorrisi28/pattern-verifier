package com.patternverifier.bridge.correct;

import com.patternverifier.annotations.GoFBridge;

@GoFBridge(implementor = Device.class)
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
