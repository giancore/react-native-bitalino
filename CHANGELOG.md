# react-native-bitalino

## 2.0.0

### Major Changes

#### 🚀 Complete iOS Support

- Full iOS platform implementation using PluxAPI.framework v1.0.2
- Feature parity with Android across all 12+ methods
- Automated CocoaPods integration with vendored_frameworks
- iOS setup automation script for one-command configuration

#### ✨ Enhanced Android API

- **Battery Control**: `battery(value: 0-63)` for analog reference voltage
- **Digital Triggers**: `trigger(channels: boolean[])` for 4 digital outputs
- **PWM Control**: `pwm(value: 0-255)` for analog output
- **Battery Level**: `getBattery()` returns current battery percentage
- **Connection Retry**: Auto-retry logic with 1s delays for reliability
- **Input Validation**: Comprehensive parameter validation with descriptive errors

#### 🔄 State Management

- 6-state connection tracking (DISCONNECTED, CONNECTING, CONNECTED, ACQUISITION_STARTED, ACQUISITION_STOPPED, ERROR)
- Real-time state change events on both platforms
- `getState()` method for current connection state query
- Platform-consistent state handling

#### 📚 Documentation Overhaul

- **7 new/updated documentation files**:
  - QUICK_START.md - Fast onboarding guide
  - DOCS_INDEX.md - Navigation hub with role-based paths
  - IOS_SETUP.md - Comprehensive iOS setup guide
  - AUTOMATED_IOS_SETUP.md - Technical implementation details
  - .github/WORKFLOWS.md - Complete CI/CD documentation
  - setup-ios.sh - Automated iOS configuration script
  - Enhanced README with badges, API reference, troubleshooting

#### 🔧 CI/CD Infrastructure

- **3 GitHub Actions workflows**:
  - CI Pipeline - Lint, type check, build validation for both platforms
  - Release Automation - Changesets with automatic NPM publishing
  - PR Validation - Conventional commits, size checks, security audit
- Automated platform validation (Android Gradle + iOS Podspec)
- Security vulnerability scanning

### Breaking Changes

#### API Structure

- Event payloads now include detailed state information
- Connection state management completely redesigned with 6 states
- Method signatures improved for cross-platform consistency

#### Migration Guide

```typescript
// v1.x - Basic Android API
await ReactNativeBitalino.connect(address);

// v2.x - Enhanced cross-platform API
await ReactNativeBitalino.connect(address);
// Now with state tracking, retry logic, and new methods:
await ReactNativeBitalino.battery(50);
await ReactNativeBitalino.trigger([true, false, true, false]);
const state = await ReactNativeBitalino.getState();
```

#### Platform Requirements

- **iOS**: Requires PluxAPI.framework v1.0.2 (see IOS_SETUP.md)
- **CI/CD**: Node 20.x (runtime requirements unchanged)

### Full Feature List

**Methods (12+)**:

- `scanBitalinoDevices()`, `connect()`, `disconnect()`
- `start()`, `stop()`, `state()`
- `battery()` ✨, `trigger()` ✨, `pwm()` ✨
- `getBattery()` ✨, `getState()` ✨, `isConnected()`, `getDeviceInfo()`

**Events (6)**:

- `onDeviceFound`, `onScanComplete`, `onConnectionStateChanged`
- `onDataReceived`, `onBatteryLevel`, `onStateChanged`

**Documentation**:

- Complete API reference with examples
- Platform differences comparison table
- Troubleshooting guides for Android and iOS
- Setup automation scripts
- CI/CD workflow documentation

### Credits

- Android: Built with [revolution-android-api](https://github.com/BITalinoWorld/revolution-android-api)
- iOS: Built with [PluxAPI.framework v1.0.2](https://www.pluxbiosignals.com)

---

## 1.0.0

### Major Changes

- Production version (Android only)
