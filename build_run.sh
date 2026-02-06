#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Building Kanaz Player...${NC}"

./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo -e "${GREEN}Build successful!${NC}"
    
    APK_PATH=$(find ./app/build/outputs/apk/debug -name "*.apk" | head -n 1)
    
    if [ -n "$APK_PATH" ]; then
        echo -e "${YELLOW}APK location:${NC} $APK_PATH"
        echo -e "${YELLOW}APK size:${NC} $(du -h "$APK_PATH" | cut -f1)"
        
        if command -v adb &> /dev/null; then
            echo -e "${GREEN}ADB detected. Checking for connected devices...${NC}"
            DEVICE_COUNT=$(adb devices | grep -v "List of devices attached" | grep -c "device$")
            
            if [ $DEVICE_COUNT -gt 0 ]; then
                echo -e "${GREEN}Found $DEVICE_COUNT connected device(s).${NC}"
                read -p "Install APK? (y/n): " -n 1 -r
                echo
                if [[ $REPLY =~ ^[Yy]$ ]]; then
                    echo -e "${GREEN}Installing APK...${NC}"
                    adb install -r "$APK_PATH"
                    
                    if [ $? -eq 0 ]; then
                        echo -e "${GREEN}Installation successful!${NC}"
                        read -p "Launch the app? (y/n): " -n 1 -r
                        echo
                        if [[ $REPLY =~ ^[Yy]$ ]]; then
                            adb shell am start -n com.gokanaz.kanazplayer/.MainActivity
                        fi
                    fi
                fi
            else
                echo -e "${YELLOW}No devices connected via ADB.${NC}"
            fi
        else
            echo -e "${YELLOW}ADB not found. Skipping installation.${NC}"
        fi
    else
        echo -e "${RED}Error: APK not found!${NC}"
        exit 1
    fi
else
    echo -e "${RED}Build failed!${NC}"
    exit 1
fi