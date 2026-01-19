# Quick Reference Card

## 📋 Setup Checklist

### Android

- [x] Install package: `npm install react-native-bitalino`
- [x] Ready to use ✅

### iOS

- [ ] Install package: `npm install react-native-bitalino`
- [ ] Download [PluxAPI.framework](https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip)
- [ ] Extract to: `node_modules/react-native-bitalino/ios/Frameworks/PluxAPI.framework/`
- [ ] Run: `cd ios && pod install && cd ..`
- [ ] Edit `ios/ReactNativeBitalinoModule.swift`:
  - [ ] Uncomment `import PluxAPI` (line ~17)
  - [ ] Uncomment `, PXBioPluxManagerDelegate` (line ~19)
  - [ ] Uncomment properties (lines ~30-32)
  - [ ] Uncomment delegate methods (lines ~280+)
- [ ] Build: `npx expo run:ios`

---

## 🚀 Quick Start Code

```typescript
import * as BITalino from "react-native-bitalino";

// 1. Scan for devices
await BITalino.scanBitalinoDevices(10); // 10 seconds

// Listen for devices
BITalino.scanBitalinoDevicesListener(({ device }) => {
  console.log("Found:", device.name, device.address);
});

// 2. Connect (with retry)
await BITalino.connectWithRetry("20:18:06:13:01:33", 3);

// Listen for connection state
BITalino.addConnectionStateChangedListener(({ state }) => {
  console.log("State:", state);
});

// 3. Start acquisition
await BITalino.start([0, 1, 2], 1000); // Channels A0-A2, 1000Hz

// Listen for frames
BITalino.addFrameReceivedListener(({ data }) => {
  console.log("Frame:", data);
});

// 4. Control device
await BITalino.trigger([1, 0, 1, 0]); // Digital outputs
await BITalino.pwm(128); // PWM output
const battery = await BITalino.getBattery();

// 5. Stop and disconnect
await BITalino.stop();
await BITalino.disconnect();
```

---

## 📊 API Quick Reference

| Method                | Parameters                | Description                  |
| --------------------- | ------------------------- | ---------------------------- |
| `scanBitalinoDevices` | `(seconds, timeout?)`     | Scan for devices             |
| `connect`             | `(address)`               | Connect to device            |
| `connectWithRetry`    | `(address, retries)`      | Connect with retry           |
| `disconnect`          | `()`                      | Disconnect                   |
| `start`               | `(channels[], frequency)` | Start acquisition            |
| `stop`                | `()`                      | Stop acquisition             |
| `battery`             | `(threshold)`             | Set battery threshold (0-63) |
| `trigger`             | `([d0,d1,d2,d3])`         | Set digital outputs (0/1)    |
| `pwm`                 | `(value)`                 | Set PWM output (0-255)       |
| `getBattery`          | `()`                      | Get battery level            |
| `isConnected`         | `()`                      | Check connection             |
| `getState`            | `()`                      | Get connection state         |
| `getDeviceInfo`       | `()`                      | Get device info              |

---

## 🎯 Common Patterns

### Scan → Connect → Acquire

```typescript
// 1. Scan
const devices = [];
const scanListener = BITalino.scanBitalinoDevicesListener(({ device }) => {
  devices.push(device);
});

await BITalino.scanBitalinoDevices(10);
scanListener.remove();

// 2. Connect to first device
if (devices.length > 0) {
  await BITalino.connectWithRetry(devices[0].address, 3);
}

// 3. Start acquisition
await BITalino.start([0, 1], 1000);

// 4. Process data
const frameListener = BITalino.addFrameReceivedListener(({ data }) => {
  // Process frame data
});

// 5. Cleanup
await BITalino.stop();
await BITalino.disconnect();
frameListener.remove();
```

### Error Handling

```typescript
try {
  await BITalino.start([0], 1000);
} catch (error) {
  switch (error.code) {
    case "NOT_CONNECTED":
      // Device not connected
      break;
    case "INVALID_CHANNELS":
      // Invalid channel configuration
      break;
    case "INVALID_FREQUENCY":
      // Frequency must be 1, 10, 100, or 1000
      break;
    default:
      console.error(error.message);
  }
}
```

---

## ⚠️ Common Issues

| Issue                  | Solution                                                         |
| ---------------------- | ---------------------------------------------------------------- |
| Device not found       | Enable Bluetooth, check range, verify BTH (not BLE)              |
| Connection failed      | Use `connectWithRetry()`, check device not paired elsewhere      |
| iOS "Module not found" | Verify PluxAPI.framework in `ios/Frameworks/`, run `pod install` |
| Android permissions    | Add Bluetooth permissions to AndroidManifest.xml                 |
| Frame rate too low     | Reduce number of channels or lower frequency                     |

---

## 🔗 Links

- 📘 [Complete README](./README.md)
- 🍎 [iOS Setup Guide](./IOS_SETUP.md)
- 🔧 [Technical Docs](./AUTOMATED_IOS_SETUP.md)
- 🌐 [BITalino.com](https://bitalino.com)
- 💬 [Community Forum](https://forum.bitalino.com)
- 🐛 [Report Issues](https://github.com/giancore/react-native-bitalino/issues)

---

**Made with ❤️ for BITalino biosignal acquisition**
