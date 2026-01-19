#!/bin/bash
# iOS Setup Helper Script for react-native-bitalino
# This script helps automate the iOS setup process

set -e

echo "================================================"
echo "React Native BITalino - iOS Setup Helper"
echo "================================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -f "package.json" ]; then
    echo -e "${RED}Error: package.json not found. Please run this script from your React Native project root.${NC}"
    exit 1
fi

echo -e "${BLUE}Step 1: Checking for PluxAPI.framework...${NC}"
FRAMEWORK_PATH="node_modules/react-native-bitalino/ios/Frameworks/PluxAPI.framework"

if [ -d "$FRAMEWORK_PATH" ]; then
    echo -e "${GREEN}✓ PluxAPI.framework found!${NC}"
else
    echo -e "${YELLOW}✗ PluxAPI.framework not found.${NC}"
    echo ""
    echo "Please download it from:"
    echo "https://www.pluxbiosignals.com/downloads/PLUX%20iOS%20API%20v1.0.2.zip"
    echo ""
    echo "Then extract and copy PluxAPI.framework to:"
    echo "$FRAMEWORK_PATH"
    echo ""
    read -p "Press Enter when you've placed the framework, or Ctrl+C to exit..."
    
    if [ ! -d "$FRAMEWORK_PATH" ]; then
        echo -e "${RED}Framework still not found. Exiting.${NC}"
        exit 1
    fi
fi

echo ""
echo -e "${BLUE}Step 2: Verifying framework structure...${NC}"
if [ -f "$FRAMEWORK_PATH/PluxAPI" ] && [ -d "$FRAMEWORK_PATH/Headers" ]; then
    echo -e "${GREEN}✓ Framework structure is valid${NC}"
else
    echo -e "${RED}✗ Framework structure is invalid${NC}"
    echo "Expected structure:"
    echo "$FRAMEWORK_PATH/"
    echo "  ├── Headers/"
    echo "  ├── Modules/"
    echo "  ├── PluxAPI"
    echo "  └── Info.plist"
    exit 1
fi

echo ""
echo -e "${BLUE}Step 3: Installing CocoaPods dependencies...${NC}"
cd ios
if [ ! -f "Podfile" ]; then
    echo -e "${RED}Error: Podfile not found in ios directory${NC}"
    exit 1
fi

echo "Running pod install..."
pod install

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ CocoaPods installed successfully${NC}"
else
    echo -e "${RED}✗ CocoaPods installation failed${NC}"
    exit 1
fi
cd ..

echo ""
echo -e "${BLUE}Step 4: Uncommenting PluxAPI code...${NC}"
MODULE_FILE="node_modules/react-native-bitalino/ios/ReactNativeBitalinoModule.swift"

if [ ! -f "$MODULE_FILE" ]; then
    echo -e "${RED}Error: ReactNativeBitalinoModule.swift not found${NC}"
    exit 1
fi

# Uncomment import PluxAPI
sed -i '' 's|// import PluxAPI|import PluxAPI|g' "$MODULE_FILE"

# Uncomment delegate conformance
sed -i '' 's|/\* , PXBioPluxManagerDelegate \*/|, PXBioPluxManagerDelegate|g' "$MODULE_FILE"

echo -e "${GREEN}✓ Code uncommented successfully${NC}"
echo ""
echo -e "${YELLOW}Note: You may need to manually uncomment additional sections in:${NC}"
echo "$MODULE_FILE"
echo "  - Lines ~30-32: centralManager and connectedDevice properties"
echo "  - Lines ~280+: delegate method implementations"

echo ""
echo "================================================"
echo -e "${GREEN}✓ Setup Complete!${NC}"
echo "================================================"
echo ""
echo "Next steps:"
echo "1. Review the changes in: $MODULE_FILE"
echo "2. Build your iOS app: npx expo run:ios"
echo "3. Check IOS_SETUP.md for troubleshooting"
echo ""
echo -e "${BLUE}Happy coding with BITalino! 🎉${NC}"
