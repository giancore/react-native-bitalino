# PluxAPI.framework Directory

## 📦 Framework Required

The PluxAPI.framework is **not included** in this repository due to:

- Licensing restrictions (proprietary software)
- File size (~15-20MB)
- Version control considerations

## 🔽 Download & Install

1. Download PluxAPI iOS API v1.0.2+ from:

   - https://www.pluxbiosignals.com/pages/download-software
   - Direct download: https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip

2. Extract and copy `PluxAPI.framework` to this directory:

   ```
   ios/Frameworks/PluxAPI.framework/
   ```

3. The framework structure should look like:

   ```
   ios/Frameworks/
   └── PluxAPI.framework/
       ├── Headers/
       │   ├── PluxAPI.h
       │   └── PluxAPI-Swift.h
       ├── Modules/
       │   └── PluxAPI.swiftmodule/
       ├── PluxAPI (binary)
       └── Info.plist
   ```

4. Run `pod install` in the `ios` directory

## After Adding the Framework

Once the framework is in place:

- CocoaPods will automatically link it
- No manual Xcode configuration needed
- The module will compile and work immediately

## Verification

Check that the framework was added correctly:

```bash
ls -la ios/Frameworks/PluxAPI.framework/
```

You should see the framework contents listed above.

## Alternative: Keep Framework in Git (Not Recommended)

If you want to commit the framework to your repository:

1. Remove `ios/Frameworks/` from `.gitignore`
2. Commit the framework: `git add ios/Frameworks/PluxAPI.framework`

**Warning:** This will increase your repository size by ~10-20MB.
