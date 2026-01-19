# React Native BITalino

[![npm version](https://img.shields.io/npm/v/react-native-bitalino.svg)](https://www.npmjs.com/package/react-native-bitalino)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS-blue.svg)](https://github.com/giancore/react-native-bitalino)
[![License](https://img.shields.io/npm/l/react-native-bitalino.svg)](https://github.com/giancore/react-native-bitalino/blob/master/LICENSE)
[![Expo](https://img.shields.io/badge/Expo-Compatible-4630EB.svg)](https://expo.dev)

Cross-platform React Native module for **BITalino biosignal acquisition devices** using Bluetooth Classic (BTH).

> **📚 New to this library?** Start with [QUICK_START.md](./QUICK_START.md) for setup checklist and code examples!  
> **📖 Full documentation index:** [DOCS_INDEX.md](./DOCS_INDEX.md)

---

## Platform Support

This plugin uses the available native APIs available at https://bitalino.com/en/development/apis.

| Platform | Status | Native Repository                                                                                         |
| :------: | :----: | :-------------------------------------------------------------------------------------------------------- |
| Android  |   ✅   | [revolution-android-api](https://github.com/BITalinoWorld/revolution-android-api)                         |
|   iOS    |   🔄   | [PluxAPI.framework v1.0.2](https://github.com/pluxbiosignals/ios-sample) (CocoaPods + framework download) |

**iOS Setup**: iOS support uses CocoaPods with automatic linking. You only need to download PluxAPI.framework once and place it in the correct location. See [IOS_SETUP.md](./IOS_SETUP.md) for step-by-step instructions (5 minutes setup).

## Features

### Core Functionality

- ✅ **Bluetooth device scanning** - Discover nearby BITalino devices (BTH only)
- ✅ **Connection with retry logic** - Automatic reconnection attempts
- ✅ **Real-time data acquisition** - Stream biosignal data at 1/10/100/1000 Hz
- ✅ **State management** - Track connection and acquisition states
- ✅ **Event-driven architecture** - React to device events in real-time

### Device Control

- ✅ **Multi-channel support** - Up to 6 analog channels (A0-A5)
- ✅ **Battery monitoring** - Get battery level and set threshold (0-63)
- ✅ **Digital outputs** - Control 4 digital channels (D0-D3)
- ✅ **PWM control** - Analog output 0-255 (BITalino 2 only)
- ✅ **Device info queries** - Get connection state, sample rate, active channels

### Platform Support

- ✅ **Android** - Full support via revolution-android-api
- 🔄 **iOS** - Full API parity via PluxAPI.framework (requires setup)
- ✅ **Expo** - Compatible with Expo Modules
- ✅ **TypeScript** - Full type definitions included

## Installation

### Android (Ready to use)

```bash
npx expo install react-native-bitalino
# or
npm install react-native-bitalino
```

### iOS (Additional setup required)

```bash
npx expo install react-native-bitalino
```

**Then follow iOS setup**: iOS requires PluxAPI.framework to be downloaded and placed in the module directory. See [IOS_SETUP.md](./IOS_SETUP.md) for the complete guide (~5 minutes).

**Quick iOS Steps:**

1. Download [PluxAPI.framework](https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip)
2. Place in `node_modules/react-native-bitalino/ios/Frameworks/`
3. Run `cd ios && pod install`
4. Uncomment 4 lines in Swift module
5. Build your app

## Usage

### API Reference

### Complete API Documentation

All methods return Promises and include comprehensive error handling with descriptive error codes.

#### Device Discovery

| Method                                        | Parameters                                                  | Returns         | Description                                                                    |
| --------------------------------------------- | ----------------------------------------------------------- | --------------- | ------------------------------------------------------------------------------ |
| `scanBitalinoDevices(scanPeriod, timeoutMs?)` | `scanPeriod`: 1-60 seconds<br>`timeoutMs`: optional timeout | `Promise<void>` | Scan for BITalino devices. Listen to `onBitalinoDeviceFound` event for results |

#### Connection Management

| Method                                  | Parameters                                         | Returns         | Description                                |
| --------------------------------------- | -------------------------------------------------- | --------------- | ------------------------------------------ |
| `connect(address)`                      | `address`: MAC address (e.g., "20:18:06:13:01:33") | `Promise<void>` | Connect to device                          |
| `connectWithRetry(address, maxRetries)` | `address`: MAC address<br>`maxRetries`: 1-10       | `Promise<void>` | Connect with automatic retry (recommended) |
| `disconnect()`                          | None                                               | `Promise<void>` | Disconnect from device                     |

#### Data Acquisition

| Method                       | Parameters                                                     | Returns         | Description            |
| ---------------------------- | -------------------------------------------------------------- | --------------- | ---------------------- |
| `start(channels, frequency)` | `channels`: [0-5] array<br>`frequency`: 1, 10, 100, or 1000 Hz | `Promise<void>` | Start data acquisition |
| `stop()`                     | None                                                           | `Promise<void>` | Stop data acquisition  |

#### Device Control

| Method                     | Parameters                                        | Returns                           | Description                        |
| -------------------------- | ------------------------------------------------- | --------------------------------- | ---------------------------------- |
| `battery(threshold)`       | `threshold`: 0-63                                 | `Promise<void>`                   | Set battery threshold (BITalino 2) |
| `trigger(digitalChannels)` | `digitalChannels`: [0/1, 0/1, 0/1, 0/1] for D0-D3 | `Promise<void>`                   | Set digital outputs                |
| `pwm(pwmOutput)`           | `pwmOutput`: 0-255                                | `Promise<void>`                   | Set PWM output (BITalino 2)        |
| `state()`                  | None                                              | `Promise<void>`                   | Request device state (BITalino 2)  |
| `getBattery()`             | None                                              | `Promise<{batteryLevel: number}>` | Get current battery level          |

#### State Queries

| Method            | Parameters | Returns              | Description                  |
| ----------------- | ---------- | -------------------- | ---------------------------- |
| `isConnected()`   | None       | `boolean`            | Check if device is connected |
| `getState()`      | None       | `string`             | Get current connection state |
| `getDeviceInfo()` | None       | `DeviceInfo \| null` | Get device information       |

### Events

Subscribe to these events to receive real-time updates:

```typescript
// Device found during scan
ReactNativeBitalino.scanBitalinoDevicesListener((event) => {
  console.log("Device:", event.device);
});

// Frame data received
ReactNativeBitalino.addFrameReceivedListener((event) => {
  console.log("Frame:", event.data);
});

// Connection state changed
ReactNativeBitalino.addConnectionStateChangedListener((event) => {
  console.log("State:", event.state);
});

// Error occurred
ReactNativeBitalino.addErrorListener((event) => {
  console.error("Error:", event.error);
});
```

### Types

```typescript
interface BitalinoDeviceInfo {
  address: string;
  name: string;
  state: string;
  sampleRate: number;
  activeChannels: number[];
}

type ConnectionState =
  | "DISCONNECTED"
  | "CONNECTING"
  | "CONNECTED"
  | "ACQUISITION_STARTED"
  | "ACQUISITION_STOPPED"
  | "ERROR";
```

## Platform Differences

### Android vs iOS

| Feature          | Android | iOS          |
| ---------------- | ------- | ------------ |
| Auto-discovery   | ✅      | ✅           |
| Connection       | ✅      | ✅           |
| Data acquisition | ✅      | ✅           |
| Battery control  | ✅      | ⚠️ Limited\* |
| Digital outputs  | ✅      | ⚠️ Limited\* |
| PWM control      | ✅      | ⚠️ Limited\* |
| State queries    | ✅      | ✅           |

\*iOS PluxAPI may not expose all low-level BITalino commands. Some features may require raw command implementation.

## Troubleshooting

### Android

**"Device not found"**

- Ensure Bluetooth is enabled
- Check device is powered on and in range
- Verify correct MAC address format
- Only BTH (Bluetooth Classic) is supported, not BLE

**"Connection failed"**

- Try `connectWithRetry()` instead of `connect()`
- Ensure device is not paired with another device
- Check Bluetooth permissions in AndroidManifest.xml

### iOS

**"Module 'PluxAPI' not found"**

- Verify PluxAPI.framework is in `ios/Frameworks/` directory
- Run `pod install` in ios directory
- Check framework structure (see `ios/Frameworks/README.md`)
- Clean build folder (Xcode: Product → Clean Build Folder)

**"Framework not loaded"**

- Ensure framework supports your architecture (arm64 for device, x86_64/arm64 for simulator)
- Verify framework is embedded and signed
- Check CocoaPods output for errors

**"Permission denied"**

- Add Bluetooth usage descriptions to Info.plist
- Check iOS Settings → Your App → Bluetooth

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Test on both Android and iOS
4. Submit a pull request

## License

See LICENSE file for details.

## Credits

- **BITalino**: https://bitalino.com
- **Android API**: [revolution-android-api](https://github.com/BITalinoWorld/revolution-android-api)
- **iOS API**: [PluxAPI iOS](https://github.com/pluxbiosignals/ios-sample)

## Support

- **Issues**: [GitHub Issues](https://github.com/giancore/react-native-bitalino/issues)
- **Documentation**: [Full API Docs](./IOS_SETUP.md)
- **BITalino Forums**: https://forum.bitalino.com

```typescript
import * as ReactNativeBitalino from "react-native-bitalino";
```

### Scan Devices

```typescript
async function scanBitalinoDeviceAsync() {
  try {
    // Scan for 10 seconds with optional timeout
    const returnValue = await ReactNativeBitalino.scanBitalinoDevices(
      10000,
      15000,
    );
    console.log(returnValue);
  } catch (error) {
    console.error(error);
  }
}
```

Create a listener to get all devices

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.scanBitalinoDevicesListener(
    ({ device }) => console.log(device),
  );

  return () => listener.remove();
}, []);
```

### Connect to Device

Simple connection:

```typescript
function connect() {
  try {
    const result = ReactNativeBitalino.connect("20:18:06:13:01:33");
    console.log(result);
  } catch (error) {
    console.error(error);
  }
}
```

Connect with retry (recommended):

```typescript
async function connectWithRetry() {
  try {
    // Try to connect up to 3 times
    const result = await ReactNativeBitalino.connectWithRetry(
      "20:18:06:13:01:33",
      3,
    );
    console.log("Connected:", result);
  } catch (error) {
    console.error("Failed to connect after retries:", error);
  }
}
```

### Check Connection State

```typescript
// Check if connected
const isConnected = ReactNativeBitalino.isConnected();

// Get current state
const state = ReactNativeBitalino.getState();
console.log("Current state:", state); // DISCONNECTED, CONNECTING, CONNECTED, etc.

// Get detailed device info
const info = await ReactNativeBitalino.getDeviceInfo();
console.log("Device info:", info);
// {
//   address: "20:18:06:13:01:33",
//   state: "CONNECTED",
//   isConnected: true,
//   sampleRate: 1000,
//   activeChannels: [0, 1, 2],
//   isAcquiring: false
// }
```

### Start Acquisition

```typescript
function start() {
  try {
    // Channels: 0-5, Frequency: 1, 10, 100, or 1000 Hz
    const result = ReactNativeBitalino.start([0, 1, 2, 3, 4, 5], 1000);
    console.log("Acquisition started:", result);
  } catch (error) {
    console.error("Invalid parameters:", error);
    // Will throw if:
    // - Not connected
    // - Invalid frequency (must be 1, 10, 100, or 1000)
    // - Invalid channels (must be 0-5)
  }
}
```

### Listen to Events

Data frames:

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.startAcquisitionListener(({ frame }) =>
    console.log(frame),
  );

  return () => listener.remove();
}, []);
```

State changes:

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.stateChangedListener(
    ({ state, stateId }) => {
      console.log("State changed:", state);
    },
  );

  return () => listener.remove();
}, []);
```

Command replies:

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.commandReplyListener((event) => {
    if (event.type === "description") {
      console.log("Is BITalino 2:", event.isBITalino2);
      console.log("Firmware version:", event.firmwareVersion);
    } else if (event.type === "state") {
      console.log("Device state:", event.data);
    }
  });

  return () => listener.remove();
}, []);
```

Errors:

```typescript
useEffect(() => {
  const listener = ReactNativeBitalino.errorListener((error) => {
    console.error("BITalino error:", error);
  });

  return () => listener.remove();
}, []);
```

### Stop Acquisition and Disconnect

```typescript
function stop() {
  try {
    const result = ReactNativeBitalino.stop();
    console.log("Acquisition stopped:", result);
  } catch (error) {
    console.error(error);
  }
}

function disconnect() {
  try {
    // Automatically stops acquisition if running
    const result = ReactNativeBitalino.disconnect();
    console.log("Disconnected:", result);
  } catch (error) {
    console.error(error);
  }
}
```

### Additional Functions

#### Battery Threshold Control

Sets the battery threshold for the low-battery LED (BITalino 2 only):

```typescript
try {
  // Set battery threshold (0-63)
  // Value represents voltage threshold
  const result = ReactNativeBitalino.battery(30);
  console.log("Battery threshold set:", result);
} catch (error) {
  console.error(error);
  // Throws if:
  // - Not connected
  // - Value out of range (0-63)
  // - Acquiring data (must stop first)
}
```

#### Get Battery Level

Requests the current battery level (BITalino 2 only):

```typescript
try {
  // Request battery state
  const response = await ReactNativeBitalino.getBattery();
  console.log(response.message);

  // Battery value will be received through state event listener
  const listener = ReactNativeBitalino.stateChangedListener((event) => {
    if (event.type === "state") {
      console.log("Battery level:", event.data.battery);
      console.log("Battery threshold:", event.data.batThreshold);
    }
  });
} catch (error) {
  console.error("BITalino 2 required for battery monitoring");
}
```

#### Digital Outputs Control

Controls the digital outputs (O1-O4):

```typescript
try {
  // Set digital outputs [O1, O2, O3, O4]
  // 1 = HIGH, 0 = LOW
  // BITalino: 4 outputs (during acquisition only)
  // BITalino 2: 2 outputs (anytime) [O1, O2]
  const result = ReactNativeBitalino.trigger([1, 0, 1, 0]);
  console.log("Digital outputs set:", result);
} catch (error) {
  console.error(error);
  // Throws if:
  // - Not connected
  // - Invalid array length
  // - Invalid values (must be 0 or 1)
}

// Examples:
// Turn on O1 and O2, turn off O3 and O4
ReactNativeBitalino.trigger([1, 1, 0, 0]);

// BITalino 2: Only 2 outputs
ReactNativeBitalino.trigger([1, 0]);
```

#### PWM Control

Sets the PWM (Pulse Width Modulation) output (BITalino 2 only):

```typescript
try {
  // Set PWM output (0-255)
  // Output voltage = 3.3V * (value + 1) / 256
  const result = ReactNativeBitalino.pwm(128);
  console.log("PWM output set:", result);
} catch (error) {
  console.error(error);
  // Throws if:
  // - Not connected
  // - Value out of range (0-255)
  // - Not BITalino 2
}

// Examples:
ReactNativeBitalino.pwm(0); // ~0.01V
ReactNativeBitalino.pwm(128); // ~1.65V
ReactNativeBitalino.pwm(255); // ~3.30V
```

#### Device State

Requests the full device state (BITalino 2 only):

```typescript
try {
  // Request state
  ReactNativeBitalino.state();

  // State will be received through command reply listener
  const listener = ReactNativeBitalino.commandReplyListener((event) => {
    if (event.type === "state") {
      console.log("Analog inputs:", event.data.analog);
      console.log("Digital inputs:", event.data.digital);
      console.log("Battery:", event.data.battery);
      console.log("Battery threshold:", event.data.batThreshold);
    }
  });
} catch (error) {
  console.error("BITalino 2 required");
}
```

## Connection States

- `DISCONNECTED` - Device is disconnected
- `CONNECTING` - Attempting to connect
- `CONNECTED` - Connected but not acquiring
- `ACQUISITION_STARTED` - Actively acquiring data
- `ACQUISITION_STOPPED` - Acquisition stopped
- `ERROR` - Error state

## Error Handling

All functions throw descriptive errors with detailed messages:

```typescript
try {
  ReactNativeBitalino.start([0, 1], 500); // Invalid frequency
} catch (error) {
  console.error(error.message);
  // "Invalid sampling rate: 500. Must be 1, 10, 100 or 1000 Hz"
}
```
