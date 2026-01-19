# 📚 Documentation Index

Welcome to the React Native BITalino documentation! Choose your starting point:

## 🚀 Getting Started

### New Users (Start Here!)

1. **[QUICK_START.md](./QUICK_START.md)** ⭐

   - Setup checklist for Android & iOS
   - Quick start code examples
   - API quick reference
   - Common patterns and error handling
   - **Recommended first read** (~5 min)

2. **[README.md](./README.md)** 📖
   - Complete feature overview
   - Installation instructions
   - Full API documentation
   - Usage examples for all methods
   - Troubleshooting guide

## 🍎 iOS-Specific Documentation

### iOS Setup (Required for iOS)

3. **[IOS_SETUP.md](./IOS_SETUP.md)** 🔧

   - Step-by-step iOS setup guide
   - PluxAPI.framework download & installation
   - CocoaPods integration
   - Bluetooth permissions
   - iOS-specific troubleshooting
   - **Must read for iOS developers** (~5 min setup)

4. **[ios/Frameworks/README.md](./ios/Frameworks/README.md)** 📦
   - Framework directory structure
   - Download instructions
   - Verification steps
   - Quick reference

## 🔬 Technical Documentation

### Advanced/Developer Documentation

5. **[AUTOMATED_IOS_SETUP.md](./AUTOMATED_IOS_SETUP.md)** 🏗️
   - Technical implementation details
   - CocoaPods configuration explained
   - Design decisions and alternatives
   - Architecture overview
   - **For maintainers and contributors**

## 📊 Documentation Summary

| Document                 | Target Audience | Time to Read | Purpose                         |
| ------------------------ | --------------- | ------------ | ------------------------------- |
| QUICK_START.md           | All developers  | 5 min        | Fast onboarding & code examples |
| README.md                | All developers  | 15 min       | Complete API reference          |
| IOS_SETUP.md             | iOS developers  | 10 min       | iOS-specific setup              |
| ios/Frameworks/README.md | iOS developers  | 2 min        | Framework installation          |
| AUTOMATED_IOS_SETUP.md   | Contributors    | 10 min       | Technical architecture          |

## 🗺️ Reading Path by Role

### Frontend Developer (React Native)

```
1. QUICK_START.md (examples & API)
2. README.md (full API reference)
3. IOS_SETUP.md (only if building for iOS)
```

### iOS Developer

```
1. QUICK_START.md (overview)
2. IOS_SETUP.md (setup guide) ⭐ IMPORTANT
3. ios/Frameworks/README.md (framework details)
4. README.md (full API)
```

### Android Developer

```
1. QUICK_START.md (overview)
2. README.md (full API)
Done! No additional setup needed ✅
```

### Module Contributor

```
1. README.md (API overview)
2. AUTOMATED_IOS_SETUP.md (architecture)
3. Source code in ios/ and android/
```

## 🔍 Find Information By Topic

### Installation & Setup

- **Android**: [README.md](./README.md#installation) → Install & run ✅
- **iOS**: [IOS_SETUP.md](./IOS_SETUP.md) → Complete setup guide 🔧

### API Usage

- **Quick examples**: [QUICK_START.md](./QUICK_START.md#quick-start-code)
- **Full reference**: [README.md](./README.md#api-reference)
- **TypeScript types**: [README.md](./README.md#types)

### Device Connection

- **Scanning**: [QUICK_START.md](./QUICK_START.md) → Scan pattern
- **Connecting**: [README.md](./README.md#connect-to-device)
- **Retry logic**: [README.md](./README.md#connect-to-device) → connectWithRetry()

### Data Acquisition

- **Starting**: [README.md](./README.md#start-acquisition)
- **Stopping**: [README.md](./README.md#stop-acquisition)
- **Frame events**: [README.md](./README.md#events)

### Device Control

- **Battery**: [README.md](./README.md#battery-monitoring)
- **Digital outputs**: [README.md](./README.md#digital-outputs-control)
- **PWM**: [README.md](./README.md#pwm-control)

### Troubleshooting

- **Common issues**: [QUICK_START.md](./QUICK_START.md#common-issues)
- **Android errors**: [README.md](./README.md#troubleshooting)
- **iOS errors**: [IOS_SETUP.md](./IOS_SETUP.md#troubleshooting)

### Platform Differences

- **Android vs iOS**: [README.md](./README.md#platform-differences)
- **iOS limitations**: [IOS_SETUP.md](./IOS_SETUP.md#platform-specific-notes)

## 🆘 Quick Help

**I'm stuck! What do I do?**

1. **Check relevant docs above** ⬆️
2. **Read troubleshooting sections** in README.md or IOS_SETUP.md
3. **Search existing issues** on [GitHub](https://github.com/giancore/react-native-bitalino/issues)
4. **Ask on BITalino forum** at https://forum.bitalino.com
5. **Create new issue** with error details

**Need a specific feature?**

- Check [README.md](./README.md#features) for supported features
- Check [README.md](./README.md#platform-differences) for platform support
- Request features via GitHub Issues

**Contributing?**

- Read [AUTOMATED_IOS_SETUP.md](./AUTOMATED_IOS_SETUP.md) for architecture
- Check source code in `ios/` and `android/` directories
- Follow code style in existing files
- Test on both platforms before PR

## 📝 Documentation Feedback

Found a typo? Documentation unclear? Have suggestions?

- **Open an issue**: [GitHub Issues](https://github.com/giancore/react-native-bitalino/issues)
- **Submit a PR**: Improve the docs directly
- **Tag as**: `documentation` label

---

**Happy coding with BITalino! 🎉**

Last updated: January 2026
