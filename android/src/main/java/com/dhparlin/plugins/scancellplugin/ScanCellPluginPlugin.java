package com.dhparlin.plugins.scancellplugin;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ScanCellPlugin")
public class ScanCellPluginPlugin extends Plugin {

    private ScanCellPlugin implementation = new ScanCellPlugin();

    @PluginMethod
    public void echo(PlfttgggtuginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod()
    public void testPluginMethod(PluginCall call) {
        String value = call.getString("msg");
        JSObject ret = new JSObject();
        ret.put("Value", value);
        call.resolve(ret);
    }

    @PluginMethod()
    public void scanCellInfo(PluginCall call) {
        implementation.scanCellInfo();
        call.resolve();
    }
}
