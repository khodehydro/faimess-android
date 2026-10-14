#!/usr/bin/env bash
# Preserve the actual test result and expose actionable diagnostics in the checks API.
set -uo pipefail

./gradlew --no-daemon :app:connectedDebugAndroidTest 2>&1 | tee device-test-output.log
result=${PIPESTATUS[0]}

if [ "$result" -ne 0 ]; then
    python3 - <<'PY'
from pathlib import Path
text = "\n".join(Path("device-test-output.log").read_text().splitlines()[-100:])
text = text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
print("::error title=Device tests failed::" + text)
PY
fi

# Test-runner cleanup may uninstall the APK and remove its externalFilesDir. The screenshot
# test saves into an emulator Downloads folder so the report survives that cleanup.
mkdir -p screenshots
if adb shell test -f /sdcard/Download/faimess-screenshots/home.png; then
    if ! adb pull /sdcard/Download/faimess-screenshots/home.png screenshots/home.png; then
        echo "::error title=Screenshot collection failed::Could not pull home.png from the emulator."
        result=1
    fi
elif [ "$result" -eq 0 ]; then
    echo "::error title=Screenshot missing::Tests passed but the expected home.png was not found."
    result=1
fi

exit "$result"
