#!/bin/bash

# Configuration
APK_PATH="/Users/apple/AndroidStudioProjects/GLOBALINDUSTRIESattendance/app/build/outputs/apk/debug/app-debug.apk"
LOCAL_IP=$(ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || echo "localhost")

echo "======================================================="
echo "  GLOBAL INDUSTRIES - APK HOSTING & DOWNLOAD UTILITY  "
echo "======================================================="
echo "APK File: $APK_PATH"
echo ""

if [ ! -f "$APK_PATH" ]; then
    echo "Error: APK file not found. Building APK now..."
    ./gradlew app:assembleDebug
fi

echo "Choose a hosting method:"
echo "1) Instant Cloud Download Link (Upload via Terminal)"
echo "2) Local WiFi Network Server (Download on same WiFi)"
echo "3) Exit"
echo ""
read -p "Select option (1-3): " choice

case $choice in
    1)
        echo ""
        echo "Uploading APK to cloud server..."
        RESPONSE=$(curl -s -F "file=@$APK_PATH" https://tmpfiles.org/api/v1/upload)
        URL=$(echo $RESPONSE | grep -o 'https://tmpfiles.org/[^"]*' | sed 's/tmpfiles.org\//tmpfiles.org\/dl\//')
        if [ -n "$URL" ]; then
            echo ""
            echo "SUCCESS! Download URL generated:"
            echo "👉 $URL"
            echo ""
            echo "Open this link in your phone's browser to download and install!"
        else
            echo "Cloud upload failed. Try local WiFi server (Option 2)."
        fi
        ;;
    2)
        echo ""
        echo "Starting local WiFi HTTP server..."
        echo "Connect your phone to the same WiFi network and open this URL in your phone browser:"
        echo ""
        echo "👉 http://$LOCAL_IP:8080/app-debug.apk"
        echo ""
        echo "Press Ctrl+C to stop server when done."
        echo ""
        cd $(dirname "$APK_PATH")
        python3 -m http.server 8080
        ;;
    3)
        echo "Exiting."
        exit 0
        ;;
    *)
        echo "Invalid option."
        ;;
esac
