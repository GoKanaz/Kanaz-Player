#!/bin/bash
echo "Checking if onClick is properly connected..."
grep -n "onClick.*togglePlayPause" app/src/main/java/com/gokanaz/kanazplayer/ui/player/PlayerScreen.kt
