# iOS Setup Guide

> **Setup Time**: ~5 minutes | **Difficulty**: Easy

## ⚡ Quick Start

If you're in a hurry:

```bash
# 1. Download framework
open https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip

# 2. Extract and copy to:
# node_modules/react-native-bitalino/ios/Frameworks/PluxAPI.framework/

# 3. Install
cd ios && pod install && cd ..

# 4. Edit ios/ReactNativeBitalinoModule.swift (uncomment 4 sections)

# 5. Build
npx expo run:ios
```

## Requirements

- iOS 13.4+
- Xcode 12+
- Swift 5.4+
- PluxAPI.framework v1.0.2+ (download link below)

## Quick Setup (Automated with CocoaPods)

### Option A: Automated Script (Recommended)

```bash
# From your React Native project root:
bash node_modules/react-native-bitalino/setup-ios.sh
```

This script will:

1. ✅ Check for PluxAPI.framework
2. ✅ Verify framework structure
3. ✅ Run `pod install`
4. ✅ Uncomment required code

Then just build: `npx expo run:ios`

### Option B: Manual Setup

Follow the steps below if you prefer manual setup or the script doesn't work.

### 1. Download PluxAPI.framework

Download the PluxAPI framework from the official PLUX website:

- https://www.pluxbiosignals.com/pages/download-software
- Look for "PLUX iOS API v1.0.2" or newer
- Direct link: https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip

### 2. Place Framework in Module Directory

Extract and copy `PluxAPI.framework` to:

```
node_modules/react-native-bitalino/ios/Frameworks/PluxAPI.framework
```

Or if developing the module locally:

```
react-native-bitalino/ios/Frameworks/PluxAPI.framework
```

**Note:** See `ios/Frameworks/README.md` for detailed structure.

### 3. Install Dependencies

```bash
cd ios
pod install
cd ..
```

**CocoaPods will automatically:**

- ✅ Link PluxAPI.framework
- ✅ Configure framework search paths
- ✅ Embed and sign the framework

No manual Xcode configuration needed! 🎉

### 4. Update Module Implementation

Uncomment PluxAPI code in `node_modules/react-native-bitalino/ios/ReactNativeBitalinoModule.swift`:

**Option A: Quick sed commands (recommended)**

```bash
# In your project root:
sed -i '' 's|// import PluxAPI|import PluxAPI|g' node_modules/react-native-bitalino/ios/ReactNativeBitalinoModule.swift
sed -i '' 's|/\* , PXBioPluxManagerDelegate \*/|, PXBioPluxManagerDelegate|g' node_modules/react-native-bitalino/ios/ReactNativeBitalinoModule.swift
```

**Option B: Manual editing**

1. Line ~17: `// import PluxAPI` → `import PluxAPI`
2. Line ~19: `/* , PXBioPluxManagerDelegate */` → `, PXBioPluxManagerDelegate`
3. Lines ~30-32: Uncomment `centralManager` and `connectedDevice` properties
4. Lines ~280+: Uncomment all delegate method implementations (large block at end)
   npx expo run:ios

# or

npx react-native run-ios

````

---

## Alternative: Manual Xcode Setup (Not Recommended)

If you prefer manual setup or CocoaPods doesn't work:

<details>
<summary>Click to expand manual instructions</summary>

1. Open your iOS app in Xcode (`ios/YourApp.xcworkspace`)
2. Copy `PluxAPI.framework` to your project directory
3. In Xcode: Target → "Frameworks, Libraries, and Embedded Content"
4. Add `PluxAPI.framework` with "Embed & Sign"
5. Complete steps 4-5 from Quick Setup above

</details>

---
Add to your app's `Info.plist`:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app needs Bluetooth to connect to BITalino devices</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app needs Bluetooth to connect to BITalino devices</string>
````

### 5. Build and Test

```bash
cd ios
pod install
cd ..
npx expo run:ios
```

## Implementation Status

### ✅ Fully Implemented (Android)

- Connection state management
- Device scan with timeout
- Connect with retry logic
- Start/stop acquisition with validation
- Battery threshold control (0-63)
- Digital outputs (trigger)
- PWM control (0-255)
- Battery level monitoring
- All events

### ⚠️ iOS Implementation Status

- ✅ Module structure and API matching Android
- ✅ Input validation for all methods
- ✅ Event definitions
- ✅ State management (BITalinoConnectionState enum)
- ⚠️ Requires PluxAPI.framework integration
- ⚠️ Some methods may require raw command implementation (battery threshold, trigger, PWM)
- ⚠️ Needs testing with physical device

### Platform-Specific Notes

#### iOS Limitations

1. **Direct Command Support**: PluxAPI.framework may not expose all BITalino commands directly (e.g., battery threshold, digital outputs, PWM). These may require sending raw protocol commands.

2. **Device Selection**: iOS BLE discovery works differently - devices must be selected from scan results rather than connecting by address string.

3. **Background Mode**: Add "Bluetooth LE background mode" in Xcode capabilities if needed.

4. **State Management**: iOS uses PluxAPI's `States` enum which maps to our `BITalinoConnectionState`.

## API Compatibility

All methods match the Android implementation:

```typescript
// Scan
await scanBitalinoDevices(15); // 15 seconds

// Connect
await connect("00:00:00:00:00:00");
await connectWithRetry("00:00:00:00:00:00", 3);

// Acquisition
await start([0, 1, 2], 1000); // channels 0-2, 1000 Hz
await stop();

// Control
await battery(30); // threshold 0-63
await trigger([1, 0, 1, 0]); // D0=1, D1=0, D2=1, D3=0
await pwm(128); // 0-255

// Query
const info = getDeviceInfo();
const connected = isConnected();
const battery = await getBattery();
```

## Troubleshooting

### "Module 'PluxAPI' not found"

- Ensure PluxAPI.framework is added to Xcode project
- Check that framework is in "Frameworks, Libraries, and Embedded Content"
- Verify "Embed & Sign" is selected
- Clean build folder (Cmd+Shift+K) and rebuild

### "Undefined symbols for architecture"

- Make sure framework is added for correct architecture (arm64 for device, x86_64/arm64 for simulator)
- PluxAPI.framework must support your target architecture

### Bluetooth Permission Denied

- Add Bluetooth usage descriptions to Info.plist
- Check iOS Settings → Your App → Bluetooth permission

### Connection Issues

- Ensure device is powered on and in range
- Only BTH (Bluetooth Classic) is supported, not BLE
- iOS may have different pairing requirements than Android

## Reference Implementation

See the official iOS sample app:

- https://github.com/pluxbiosignals/ios-sample

Key files:

- `DeviceManager.swift` - Shows PluxAPI usage patterns
- `AcquisitionView.swift` - Start/stop acquisition example
- `DeviceInfoView.swift` - Battery and properties example

## Next Steps

1. Complete framework integration following steps above
2. Test with physical BITalino device
3. Implement raw command support if needed for battery/trigger/PWM
4. Test all API methods for iOS/Android parity

## FAQ

### Why isn't PluxAPI.framework included in the repository?

For three main reasons:

1. **Licensing** - PluxAPI is proprietary software owned by PLUX
2. **Size** - Framework is ~15-20MB, would bloat the npm package
3. **Updates** - Users can download the latest version directly from PLUX

### Can I use this with Bluetooth Low Energy (BLE)?

No. BITalino uses Bluetooth Classic (BTH), not BLE. Make sure your iOS device and BITalino support BTH.

### Does this work with Expo Go?

No. This module uses native code and requires a custom development build. Use:

```bash
npx expo run:ios
```

### What if I need a different PluxAPI version?

Download your preferred version from PLUX and place it in the same location. The module uses whatever framework you provide.

### Can I commit the framework to my app repository?

Yes, if it's your app repository (not this module). Copy the framework to your app's `ios/Frameworks/` and add to git. Update your app's Podfile accordingly.

## Related Documentation

- 📘 [Main README](./README.md) - Complete API reference
- 🔧 [AUTOMATED_IOS_SETUP.md](./AUTOMATED_IOS_SETUP.md) - Technical implementation details
- 📦 [ios/Frameworks/README.md](./ios/Frameworks/README.md) - Framework structure guide
- 🌐 [PluxAPI Documentation](https://www.pluxbiosignals.com) - Official PLUX docs
- 💬 [BITalino Forums](https://forum.bitalino.com) - Community support

## Changelog

### v1.0.0 (Current)

- ✅ Initial iOS support with PluxAPI.framework
- ✅ Automated CocoaPods integration
- ✅ Full API parity with Android
- ✅ Comprehensive documentation
- ⚠️ Some features may require raw command implementation

---

**Need help?** Open an issue on [GitHub](https://github.com/giancore/react-native-bitalino/issues)
