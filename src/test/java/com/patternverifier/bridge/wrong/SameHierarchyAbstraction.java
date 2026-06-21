package com.patternverifier.bridge.wrong;

import com.patternverifier.bridge.correct.Device;

// VIOLAZIONE 3: Abstraction implementa Implementor — le due gerarchie si sovrappongono.
// Nel Bridge le gerarchie devono essere indipendenti; se Abstraction implementa
// Implementor, non c'è separazione e il pattern perde il suo valore.
public class SameHierarchyAbstraction implements Device {

    protected Device device;

    @Override
    public void turnOn() { device.turnOn(); }

    @Override
    public void turnOff() { device.turnOff(); }

    @Override
    public void setVolume(int volume) { device.setVolume(volume); }
}
