
package com.phomemoprint;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;

import android.util.Log;

import com.module.mprinter.PrinterKit;
import com.module.mprinter.bluetooth.Bluetooth;
import com.module.mprinter.bluetooth.Device;
import com.module.mprinter.element.zxing.ErrorCorrectionLevel;
import com.module.mprinter.element.zxing.QRCodeUtil;

public class RNPhomemoPrintModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private int ScanCompleted;
  private Promise mPrintPromise;

  public RNPhomemoPrintModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNPhomemoPrint";
  }

  @ReactMethod
  public void show(String text, Promise promise) {
    promise.resolve(text + "369");
  }
  
    @ReactMethod
  public void init(Promise promise) {
    PrinterKit.init(this.reactContext.getApplicationContext());
    promise.resolve("RNPHOMEMO Init Completed");
  }

    @ReactMethod
    public void connect(String name,String mac,Promise promise) {
        PrinterKit.connect(name,mac);
        promise.resolve("OK");
    }

    @ReactMethod
    public void printQrCode(String content,Integer amount,Promise promise) {
      Bitmap code = QRCodeUtil.createQRCode(content, ErrorCorrectionLevel.H,200);
      PrinterKit.printBitmap(code,amount);
        promise.resolve("OK");
    }


  @ReactMethod
  public void scan(Promise promise) {

      JSONArray Devices = new JSONArray();
    PrinterKit.scan(new Bluetooth.PrinterDiscoveryListener() {
        
        @Override
        public void onStart() {
            Log.d("JSONObjectPut","started");
        }

        @Override
        public void onFound(Device device) {
            // Callback method when the Bluetooth search for a printer device,
            // Callback will be made for every printer device found,
            // need to actively record the list of searched printer devices, the user subsequent connection operation

            // where the parameters: Device device, records the name of the printer, MAC address and Bluetooth signal strength, the subsequent connection, will be used
            // private String mac;
            // private String name;
            // private int rssi;

            try {
                JSONObject obj = new JSONObject();
                obj.put("name", device.getName());
                obj.put("mac", device.getMac());
                obj.put("rssi", device.getRssi());

                Devices.put(obj);
                Log.d("JSONObjectPut",obj.toString());
            } catch (Exception e) {
                Log.d("JSONObjectError",e.toString());
            }
        }

        @Override
        public void onFinished() {
            if(Devices.length() > 0) {
                promise.resolve(Devices.toString());
            }
            promise.resolve("[]");
        }
    });
  }

}