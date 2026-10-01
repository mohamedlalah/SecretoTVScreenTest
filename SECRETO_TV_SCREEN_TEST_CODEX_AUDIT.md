# Secreto TV Screen Test — Codex Audit

## 1. Project Summary

Implemented local v1.0 in the existing Android Studio project, without creating another project or repository. The app has a dark TV home screen, eight tools, six programmatic visual-test viewers, Android-reported display information, About/privacy information, and four resource locales. It assists visual inspection; it does not diagnose, measure, or certify panel condition.

Validation: successful local debug build, Android lint, debug and release manifest inspection, and Google TV API 36 emulator smoke testing. No physical television or TV box was tested. Nothing was published. No release APK, release keystore, or release signing configuration was created.

## 2. Project Configuration

| Setting | Final value |
|---|---|
| Namespace / application ID | `com.secreto.tvscreentest` |
| minSdk | 26 |
| targetSdk | 36, unchanged |
| compileSdk | 37.0, changed from 36.1 for dependency compatibility |
| versionCode | 1 |
| versionName | 1.0.0 |
| Gradle wrapper | 9.5.0, unchanged |
| AGP | 9.3.3, unchanged; built-in Kotlin support |
| Kotlin catalog / Compose compiler plugin | 2.2.10, unchanged; resolved Kotlin stdlib also 2.2.10 |
| Java source / target compatibility | 11, unchanged |
| Gradle daemon toolchain | 21, existing daemon configuration |
| Compose BOM | 2024.09.00, unchanged |
| Resolved Compose UI / Foundation | 1.10.3, selected by existing dependency graph |
| Compose for TV Foundation | 1.0.0, unchanged |
| Compose for TV Material | 1.1.0, unchanged |

The original template did not pass AAR metadata verification: Core/Core KTX 1.19.0 and Lifecycle Runtime Compose 2.11.0 require compile SDK 37. Only compileSdk was raised to the already installed 37.0 platform. Gradle, AGP, Kotlin, Compose BOM, AndroidX versions, targetSdk, and minSdk were not upgraded. The BOM is not the sole determinant of resolved Compose versions; see `validation/build-and-dependencies.log`.

## 3. Files Created

Paths below are relative to this project root.

- `app/src/main/java/com/secreto/tvscreentest/Screens.kt`: tool catalog, eight-card home, focusable card, common page/detail layouts, About screen.
- `app/src/main/java/com/secreto/tvscreentest/TestViewer.kt`: patterns, remote controls, help timer, window lifecycle handling, Canvas rendering.
- `app/src/main/java/com/secreto/tvscreentest/DisplayInfoScreen.kt`: platform display/device data and display-change listener.
- `app/src/main/res/values-ar/strings.xml`: Arabic strings.
- `app/src/main/res/values-fr/strings.xml`: French strings.
- `app/src/main/res/values-es/strings.xml`: Spanish strings.
- `app/src/main/res/drawable/ic_dead.xml`, `ic_backlight.xml`, `ic_color.xml`, `ic_gradient.xml`, `ic_overscan.xml`, `ic_uniformity.xml`, `ic_info.xml`, `ic_about.xml`: eight locally authored vector icons.
- `app/src/main/res/drawable/ic_secreto.xml`: locally authored app icon.
- `app/src/main/res/drawable-xhdpi/tv_banner.png`: locally rendered 320 × 180 TV launcher banner. Uses locally available Arial for the brand text; no downloaded artwork or icon pack.
- `validation/`: build/dependency and lint logs, merged manifest copies, emulator screenshots, pixel/round-trip results, app-window excerpts, launcher resolver result, and crash-check summary. These are review evidence, not app assets, and are not packaged in the APK.
- `SECRETO_TV_SCREEN_TEST_CODEX_AUDIT.md`: this audit, written after implementation and validation.

## 4. Files Modified

- `app/build.gradle.kts`: versionName updated to 1.0.0; compileSdk corrected to 37.0 as explained above.
- `app/src/main/AndroidManifest.xml`: custom icon and proper TV banner references; removed unused tools namespace. Existing launcher categories, optional touchscreen/Leanback features, and RTL support retained.
- `MainActivity.kt`: removed sample Greeting and preview; added simple screen state and BACK handling.
- `ui/theme/Theme.kt`: fixed dark navy/cyan/violet TV theme, retaining Compose for TV Material.
- `ui/theme/Type.kt`: retained typography, removed unused sample comment block.
- `app/src/main/res/values/strings.xml`: complete default English strings.
- Removed unused template `ui/theme/Color.kt` and five `mipmap-*/ic_launcher.webp` robot icons after replacing the launcher artwork.

`settings.gradle.kts`, root `build.gradle.kts`, version catalog, wrapper, daemon configuration, and XML base theme remain unchanged. No dependencies were added.

## 5. Application Architecture

One ComponentActivity hosts Compose. `screen` and `lastTool` are saveable integer state: -1 is Home, 0–5 are visual tests, 6 is Display Info, and 7 is About. No navigation framework, repository layer, database, service, or network layer is needed.

Home requests the first tool on initial launch, then the last selected tool after returning. Viewer index/help visibility are saveable state keyed to the tool; opening a fresh viewer starts with its first pattern and help visible. BACK is handled at the activity composition for every non-home screen. BACK from Home follows normal activity behavior.

The display listener exists only while Display Info is composed and is unregistered on disposal. The help timer is a composition-scoped coroutine. No app-owned background worker/service is created.

## 6. Home Screen

Exactly eight tools, in the requested order:

1. Dead Pixel Test
2. Backlight Test
3. Color Test
4. Gradient Test
5. Overscan Test
6. Screen Uniformity
7. Display Info
8. About

A four-column, two-row grid uses explicit `FocusRequester` neighbors. Start/end neighbors respect RTL; up/down preserve the column. At outer boundaries, focus stays on the card; the grid remains traversable without wrapping. Focus has a 3 dp cyan border and lighter fill. Unfocused cards have a 1 dp muted border. Compose clickable handles center/ENTER activation; touch is optional.

The home body can scroll if localization/font sizing makes it taller. Detail screens keep a focused Home button and explicitly handle UP/DOWN to scroll longer content; OK activates that button. System BACK also works. Header includes the app title, subtitle, SecretoTools attribution, and developer name. No diagnostic assertions are made.

## 7. Test Viewer

- Canvas fills the full Compose window with no padding or underlying card decoration.
- LEFT/RIGHT cycle backward/forward with wraparound. These remain physical LEFT/RIGHT in Arabic.
- Center, ENTER, and numpad ENTER toggle help on key-up; their key-downs are consumed to avoid multiple toggles.
- Help starts visible and hides after five seconds. Changing a pattern while help is visible restarts that timer. Hidden help stays hidden during pattern changes.
- BACK is not consumed by the viewer key handler; MainActivity returns to Home.
- `FLAG_KEEP_SCREEN_ON` is added while the viewer exists. Disposal clears it only if it was not present before entry.
- `WindowCompat.setDecorFitsSystemWindows(false)` and `WindowInsetsControllerCompat.hide(systemBars())` request immersive rendering. Transient bars may appear through system gestures.
- Previous system-bar visibility and controller behavior are captured and restored on disposal. Decor fitting returns to the activity's normal `true` baseline; Android 15+ edge-to-edge enforcement may override platform fitting behavior.
- No brightness, display mode, refresh-rate, color-mode, or permanent system setting is changed.

Emulator window excerpts demonstrate KEEP_SCREEN_ON during a test, its removal on Home, and restoration of the controller behavior from SHOW_TRANSIENT_BARS_BY_SWIPE to DEFAULT. Actual system bars on this TV emulator are normally absent, so physical-device bar behavior remains a checklist item.

## 8. Test Pattern Implementation

### Dead Pixel Test

Eight uniform Canvas fills: black, white, red, green, blue, cyan, magenta, yellow. Full-frame emulator pixel verification confirmed the corresponding eight exact RGB values when help was hidden. No gradients, text, or borders remain in these frames.

### Backlight Test

Four uniform RGB levels: #000000, #080808, #202020, #808080. These support visual inspection of dark areas and light leakage without interpreting the result.

### Color Test

Red, green, blue, cyan, magenta, yellow, white, then eight equal-width vertical bars: white, yellow, cyan, green, magenta, red, blue, black. Bars are drawn programmatically; slight rectangle overlap prevents fractional-width gaps. These are simple visual color bars, not a calibrated broadcast standard.

### Gradient Test

Four horizontal `Brush.horizontalGradient` fills: black to white, red, green, and blue. Rendered through normal Compose/Android color handling. They are not HDR, calibrated gamma, or a guarantee of a particular panel bit depth.

### Overscan Test

Dark background, 2-pixel outer white stroke centered one pixel from the Canvas edge, cyan inset rectangle at 5% on each axis, horizontal/vertical center lines, a cyan center circle/crosshair, and yellow corner L markers. The 5% guide explanation appears only in help. It reveals cropping of the app image, subject to Android/TV scaling and overscan behavior.

### Screen Uniformity

Four uniform fills: white #FFFFFF, light gray #CCCCCC, approximately 50% encoded gray #808080, dark gray #202020. “50%” refers to encoded RGB level, not measured luminance or display brightness.

## 9. Display Info

| Field | Source / behavior |
|---|---|
| App window resolution | Outer Compose Box `Modifier.onSizeChanged`, physical pixel dimensions of the current app content surface; initially Not available until measured |
| Current display mode dimensions | `Display.mode.physicalWidth` and `physicalHeight` |
| Refresh rate | `Display.refreshRate`; rejects nonpositive/nonfinite values |
| Density / logical DPI | `activity.resources.displayMetrics.density` and `densityDpi`; not physical panel DPI |
| Android version | `Build.VERSION.RELEASE` |
| Manufacturer/model | `Build.MANUFACTURER` and `Build.MODEL`; blank/UNKNOWN mapped to Not available |
| HDR | `Display.hdrCapabilities.supportedHdrTypes` |

API 30+: activity-associated `Activity.display`. API 26–29: `activity.windowManager.defaultDisplay`, with narrowly scoped deprecation suppression. Display mode and HDR APIs exist below minSdk 26. HDR IDs map only reported values to Dolby Vision, HDR10, HLG; reported value 4 maps to HDR10+ only on API 29+. Unknown IDs are not assigned guessed names. Null capability data shows Not available; an empty list shows None reported; a list with no recognized types shows Not available.

`DisplayManager.registerDisplayListener` on the main looper triggers fresh reads when displays change; the listener is removed on disposal. This is not continuous measurement of instantaneous frame timing. The emulator reported 1920 × 1080, 60 Hz, density 2.0 / 320 logical DPI, and no HDR types. Those are emulator observations, not hardcoded values.

Window/compositor resolution may be lower than the connected physical panel. Android/vendor-reported display modes and HDR flags are not independent panel measurements, and do not establish Netflix/YouTube/Prime Video playback capabilities. The UI explicitly explains those limits.

## 10. Localization

- `values/`: English default.
- `values-ar/`: Arabic.
- `values-fr/`: French.
- `values-es/`: Spanish.

All four string files have the same 62 resource keys; XML parsing/resource parity checks passed. Titles, subtitles, instructions, pattern labels, information labels, privacy text, and About copy use resources. Technical HDR names and the version token are intentionally invariant. Literal percent labels use `formatted="false"`; other placeholders retain numbered formatter arguments.

Manifest `supportsRtl=true` and Compose's inherited layout direction mirror Arabic layout. Start/end focus links mirror home navigation; the viewer intentionally retains physical left/right controls. Arabic Home and help were visually inspected, and LEFT from the first rightmost card moved to the adjacent card on its left. English, French, and Spanish Home screenshots were inspected. The long Spanish backlight label was shortened to “Luz de fondo.” No in-app language selector is included; the app follows the configured Android/app locale. Final emulator per-app locale override was cleared.

Full linguistic review and all detail screens at unusually large accessibility font sizes remain physical-device/reviewer work.

## 11. Android TV Compatibility

MAIN includes both LAUNCHER and LEANBACK_LAUNCHER. `android.hardware.touchscreen` and `android.software.leanback` are both optional, as in the original template. No new required hardware features were added. Manifest references the custom vector icon and 320 × 180 TV banner.

The emulator's package resolver successfully resolved LEANBACK_LAUNCHER to `com.secreto.tvscreentest/.MainActivity`; see `validation/launcher-resolution.txt`. Actual home-launcher tile presentation on each physical device remains untested.

All interaction paths use D-pad/OK/BACK. Minimum SDK remains 26. API compatibility was checked by compilation/lint and source review; runtime testing was on API 36 only, not API 26 or Android TV 10.

## 12. Permissions and Privacy Audit

Both debug and release merged manifests were generated and inspected. Copies are in `validation/merged-debug-manifest.xml` and `validation/merged-release-manifest.xml`.

The complete requested-permission list in BOTH manifests is:

- `com.secreto.tvscreentest.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` — introduced by AndroidX Core; also declared with signature protection. This is app-private receiver protection, not a dangerous user permission.

The AndroidX ProfileInstallReceiver has a component-level `android:permission="android.permission.DUMP"` requirement on callers. The app does **not** request DUMP as a uses-permission. This receiver and AndroidX Startup initializers are template dependency contributions, not newly authored app background services.

| Item | Present? |
|---|---|
| INTERNET | No |
| Storage/media permission | No |
| Location permission | No |
| Bluetooth permission | No |
| QUERY_ALL_PACKAGES | No |
| Accessibility Service | No |
| App background service | No |
| Ads/analytics/tracking/account/server code | No |
| Root/ADB functionality in the app | No |

ADB was used only as host-side development tooling for the emulator. There is no ADB feature or invocation in the Android app. Template `allowBackup=true` remains; the app creates no account, file database, or user-content store. Transient saved UI state is used. Debug tooling contributes preview/test activities in the debug manifest; these are not production app screens.

## 13. Dependencies Audit

No runtime or test dependency was added beyond the original template. Important direct runtime entries:

- AndroidX Core KTX 1.19.0.
- AppCompat 1.8.0.
- Activity Compose 1.13.0.
- Lifecycle Runtime KTX 2.11.0.
- Compose BOM 2024.09.00, UI, UI Graphics, UI Tooling Preview.
- TV Foundation 1.0.0 and TV Material 1.1.0.

Compose Foundation/layout and coroutines used by the implementation are already transitively available. The existing graph resolves Compose UI/Foundation to 1.10.3 and Lifecycle Runtime Compose to 2.11.0. Debug-only Compose tooling/test-manifest entries and the existing Android-test dependencies were retained. Dependency resolution evidence is in `validation/build-and-dependencies.log`. No HTTP SDK, analytics SDK, image loader, navigation framework, or external icon dependency was added.

## 14. Build Verification

Final exact command, run from the project root:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:assembleDebug :app:lintDebug --console=plain
```

Result: **BUILD SUCCESSFUL**, 46 actionable tasks (12 executed, 34 up-to-date), 2 seconds for the final incremental pass. Full final output: `validation/final-build.log`. This is an incremental build, not a clean-build claim.

Additional successful command used to inspect release merging and resolved dependencies, without creating a release APK:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:assembleDebug :app:lintDebug :app:processReleaseManifest :app:dependencies --configuration debugRuntimeClasspath --console=plain
```

Output: `app/build/outputs/apk/debug/app-debug.apk`, approximately 11 MB. SHA-256:

`657d4faf6951264e07787a97bb0e106e22eebf4dad1e6dfd929916535784078a`

Gradle's standard debug task automatically applies the development/debug signature needed for emulator installation. No release signing was configured or performed, and no release artifact was produced. This is not a distributable release build.

Issues encountered and resolved:

1. Sandbox denied access to the existing global Gradle cache lock; reran with approved build access.
2. Original template AAR metadata required SDK 37; compileSdk raised to installed 37.0.
3. Kotlin `IntArray.mapNotNull` was unavailable; converted reported HDR array to a list before mapping.
4. Lint rejected literal percent strings as format strings; marked the two labels as non-format resources.
5. Removed unused sample icon resources and rewrote logical-DPI formatting to avoid a false plural candidate.

Final lint: **0 errors, 7 warnings**. Six are preserved target/version advisories (target 36, Gradle, AGP, Core, Compose BOM, Kotlin). One is IconMissingDensityFolder because the TV banner deliberately has the standard single xhdpi 320 × 180 asset; app/tool icons are vectors. No warning was hidden with a lint baseline. An earlier packaging pass reported that `libandroidx.graphics.path.so` could not be stripped and was packaged as-is. No Kotlin compilation warnings were printed in the final pass.

Emulator validation performed on the existing Television_1080p AVD, Google TV Android 16/API 36, arm64, 1920 × 1080:

- Installed and launched the debug APK, including final APK after the Spanish copy adjustment.
- Opened all eight tools through remote key events and returned with BACK.
- Six viewer LEFT/RIGHT round trips produced identical clean screenshots.
- Hidden dead-pixel frames for all eight colors matched exact expected RGB values across the entire captured frame.
- Help toggled with OK; automatic hiding after five seconds was checked; ENTER open/toggle path checked.
- Initial focus and return focus inspected; Arabic mirrored focus verified.
- KEEP_SCREEN_ON disposal and system-bar behavior restoration checked through window dumps.
- Display Info and About inspected visually.
- English, Arabic, French, Spanish Home inspected; Arabic viewer help inspected.
- Leanback activity resolver verified.
- Crash buffer had no entries for this package. It contained historical emulator Bluetooth-process crashes, which were unrelated; only the relevant summary is retained.

Evidence: `validation/smoke-results.json`, `extra-results.json`, screenshots, window excerpts, launcher output, and `app-crash-check.txt`. No instrumentation/JUnit suite or physical-device tests were run. Validation screenshots initially captured unrelated underlying emulator-app transition frames during locale recreation; those localized Home captures were retaken after waiting for recreation to settle.

## 15. Known Limitations

- Physical devices and API 26–35 runtime behavior have not been tested. Emulator results do not prove hardware compatibility or panel characteristics.
- HDR flags depend on Android, vendor firmware, output mode, and connection path; they do not guarantee HDR media playback. Tests themselves render ordinary SDR colors.
- App surface size, Android display mode, and physical panel resolution can differ. Density is logical Android density.
- Refresh rate is a platform report, not a frame-timing measurement. Dynamic refresh/HDMI changes need physical testing.
- Gradients and gray levels depend on the compositor, output range, color processing, scaling, and TV picture settings. No calibration, gamma measurement, or hardware verdict is offered.
- Overscan shows cropping of the app image; OS/vendor scaling or safe areas can affect interpretation.
- Immersive behavior and system overlays vary by TV firmware. System bars/volume dialogs may temporarily cover a pattern.
- Very large fonts, unusual aspect ratios/density overrides, and remote key-repeat behavior require device checks. Detail pages support remote scrolling.
- Language resources have parity and visual smoke checks, but have not received independent native-speaker review of every screen.
- No 4K rendering guarantee, native physical-pixel mapping guarantee, display-mode switching, or HDR output mode is implemented.
- Seven non-blocking lint warnings remain as described above. No release minification/signing/build was evaluated.

## 16. Real Device Test Checklist

All boxes below remain unchecked. Record firmware, Android version, TV/panel connection, output mode, and remote model for each run.

| Check | Chromecast / Sabrina | Xiaomi Android TV / Google TV | Youin Box Android TV 10 |
|---|---|---|---|
| Install and cold launch | [ ] | [ ] | [ ] |
| Launcher visibility, banner, icon, title | [ ] | [ ] | [ ] |
| First-card focus and obvious border | [ ] | [ ] | [ ] |
| Every direction through both home rows; no trap | [ ] | [ ] | [ ] |
| OK/ENTER activates all eight tools | [ ] | [ ] | [ ] |
| Dead Pixel: all eight full-screen solid colors | [ ] | [ ] | [ ] |
| Backlight: all four levels | [ ] | [ ] | [ ] |
| Color: seven solids and clean color bars | [ ] | [ ] | [ ] |
| Gradients: all four endpoints/transitions | [ ] | [ ] | [ ] |
| Overscan: edge, corners, safe guide, crosshair | [ ] | [ ] | [ ] |
| Uniformity: all four levels | [ ] | [ ] | [ ] |
| LEFT/RIGHT wraparound and held-key behavior | [ ] | [ ] | [ ] |
| OK help toggle and five-second auto-hide | [ ] | [ ] | [ ] |
| BACK from every screen and every pattern | [ ] | [ ] | [ ] |
| Return focus; BACK from Home exits normally | [ ] | [ ] | [ ] |
| Keep-awake in viewer; restored sleep behavior after exit | [ ] | [ ] | [ ] |
| Immersive entry/exit; volume/system overlays | [ ] | [ ] | [ ] |
| Display Info values vs Android output settings | [ ] | [ ] | [ ] |
| HDR reports only supported Android types; test no-HDR case | [ ] | [ ] | [ ] |
| HDMI/output-mode changes refresh displayed values | [ ] | [ ] | [ ] |
| About copy/version; remote scrolling | [ ] | [ ] | [ ] |
| Arabic RTL layout, focus, help and detail screens | [ ] | [ ] | [ ] |
| French: all screens, wrapping and instructions | [ ] | [ ] | [ ] |
| Spanish: all screens, wrapping and instructions | [ ] | [ ] | [ ] |
| Large fonts and actual viewing-distance readability | [ ] | [ ] | [ ] |
| Offline operation and no permission prompts | [ ] | [ ] | [ ] |
| Launch speed, repeated navigation, extended viewing | [ ] | [ ] | [ ] |
| Background/resume, process recreation, no crashes/ANRs | [ ] | [ ] | [ ] |

## 17. Release Readiness

**READY FOR REAL-DEVICE TESTING**

Local build/lint and emulator smoke checks passed. This is not READY FOR RELEASE. Physical-device verification, independent review, and explicit release/signing authorization remain outstanding.

## 18. Reviewer Notes

Review the compileSdk correction and resolved dependency graph carefully: the mismatch already existed in the template. No dependency upgrades were used to conceal it.

Inspect `TestViewer.kt` for color/gray ordering, full-frame drawing, key consumption, and disposal of window changes. Confirm return focus and RTL traversal in `Screens.kt` on actual remotes. Inspect Display Info's distinction between app resolution, display mode, logical DPI, and physical panel capabilities. Check unknown HDR data and vendor behavior on the target boxes.

The complete debug/release requested permission lists are included above; retain the distinction between the app-private signature permission and the ProfileInstallReceiver's caller-side DUMP requirement. Debug tooling is not a release permission source. Recheck the final merged release manifest when a release is eventually authorized.

The current debug APK was installed only on the local emulator. There was no publication, GitHub repository creation, store submission, release APK, release keystore, or release signing setup. No sample Greeting code or required-functionality TODO remains. This audit records observed results and known gaps rather than claiming release certification.

## 19. Display Info Clarification After Xiaomi 4K Testing

The user reports successful real-device testing on a Xiaomi 4K Android TV. Their ADB diagnostics showed Android DisplayManager modes of 1920×1080 at 60 Hz and 50 Hz, while SurfaceFlinger/vendor diagnostics reported `curTimingWidth=3840`, `curTimingHeight=2160`, `panelWidth=3840`, `panelHeight=2160`, and a 1920×1080 display region. The Android Full HD UI source is scaled to the 4K panel. These are user-supplied diagnostic findings, not new diagnostics performed by Codex. The earlier sections describe the original implementation's validation history, before this physical-device report.

Changed only `display_resolution` and `info_note` in the English, Arabic, French, and Spanish string resources. The English label is now “Android-reported display mode.” The English disclaimer is:

> Android may report the UI rendering resolution rather than the TV panel's native resolution. For example, a 4K TV may report 1920×1080 when Android renders its interface at Full HD and the TV scales it to 4K. HDR reporting does not guarantee streaming-service playback.

Equivalent natural translations are provided for Arabic, French, and Spanish. Resolution detection remains exactly unchanged; no native-panel detection, SurfaceFlinger parsing, shell/ADB access, hidden/privileged/vendor API, reflection, root, or device database was added to the app. All Kotlin source, UI design/layout/navigation, tools, patterns, About screen, package/version/SDK settings, dependencies, and manifest remain unchanged, verified against pre-edit file hashes.

Validation command:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:compileDebugSources :app:lintDebug :app:processDebugManifest --console=plain
```

Result: **BUILD SUCCESSFUL** (8 seconds; 27 actionable tasks: 14 executed, 13 up-to-date). This compiled the debug sources and linked resources without packaging or signing an APK. Lint reported zero errors and the same seven non-blocking warnings. All four resource files still contain 62 matching keys. The merged manifest still requests only the existing AndroidX app-private `com.secreto.tvscreentest.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`; no new permission or dependency was added. No publication, release, signing, or GitHub operation was performed. The revised wording has not been retested on the physical Xiaomi TV by Codex.

Exactly five maintained project files were modified in this correction:

- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-ar/strings.xml`
- `app/src/main/res/values-fr/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `SECRETO_TV_SCREEN_TEST_CODEX_AUDIT.md`

Gradle also refreshed generated build/cache/report outputs. Existing validation evidence and the original debug APK were not replaced by this compilation-only check.

## 20. Approved Artwork Startup Splash

Added only launch/splash behavior using the supplied root `splash_screen_source.png`. The approved image is 1672 × 941 pixels (approximately 16:9). It was converted without resizing, cropping, or pixel changes to lossless `app/src/main/res/drawable-nodpi/splash_artwork.webp`: 982,796 bytes versus the 1,362,276-byte PNG. Decoded RGBA pixels were compared and are identical. Android loads only the packaged WebP; the root PNG is unchanged and may be removed after retaining any desired design backup.

The launcher activity now uses `Theme.SecretoTVScreenTest.Launch`, then switches to the existing app theme in `onCreate`. The launch and app window backgrounds are opaque dark navy (#080F20), including under the system light theme. This prevents a default white window from appearing behind the initial content without changing the Compose screen colors or layouts.

Android 12/API 31+: native splash attributes show the existing brand icon on navy. The platform's `SplashScreen.setOnExitAnimationListener` runs when app content is ready. Its full-window `SplashScreenView` then receives the approved artwork, hides the system icon, and fades out over 180 ms before removal. The full artwork is used in the app-controlled exit transition, not squeezed into the system icon slot. API 26–30: the navy starting window is followed by a non-focusable ImageView over the original Compose content, removed after the same first-frame fade. `FIT_CENTER` preserves the source aspect ratio without cropping or stretching; any small unused edges remain navy. The progress-bar graphic is part of the approved static image, not a simulated loading indicator.

The fade starts after the artwork's first pre-draw/next frame. There is no hold timer, minimum loading duration, extra activity, or new navigation state. Normal resumes do not add another artwork view, and saved-state recreation skips the custom splash. The temporary view is non-clickable, non-focusable, and excluded from accessibility traversal, preserving the Home focus logic. Platform behavior follows [Android's splash-screen guidance](https://developer.android.com/develop/ui/views/launch/splash-screen) and the public [SplashScreenView API](https://developer.android.com/reference/android/window/SplashScreenView).

Created exactly these four maintained files:

- `app/src/main/java/com/secreto/tvscreentest/StartupSplash.kt`
- `app/src/main/res/drawable-nodpi/splash_artwork.webp`
- `app/src/main/res/values/splash_colors.xml`
- `app/src/main/res/values-v31/themes.xml`

Modified exactly these four maintained files:

- `app/src/main/java/com/secreto/tvscreentest/MainActivity.kt` — select the normal theme and install the startup transition; existing screen/navigation composition unchanged.
- `app/src/main/AndroidManifest.xml` — assign the launch theme to MainActivity; launcher categories and all other entries preserved.
- `app/src/main/res/values/themes.xml` — navy window background and launch-theme definitions.
- `SECRETO_TV_SCREEN_TEST_CODEX_AUDIT.md` — this section.

Validation command:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:compileDebugSources :app:lintDebug :app:processDebugManifest --console=plain
```

Final result: **BUILD SUCCESSFUL**, 2 seconds, 27 actionable tasks (11 executed, 16 up-to-date). Compilation/resource linking passed; lint reports zero errors and the same seven existing warnings. An initial lint error identified `windowLightNavigationBar` as API 27+, so that optional attribute was moved out of the minSdk-26 base theme into the API-31 resource variant. No lint suppression was introduced.

File hashes confirm that all existing tool/screens/patterns, Display Info, About, localization, Gradle files, version catalog, package/version/SDK settings, and source PNG remain unchanged. No dependencies were added. The newly merged debug manifest still requests only the existing AndroidX `com.secreto.tvscreentest.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`; no Internet or new runtime permission was introduced.

This pass compiled without packaging/signing an APK and did not publish, create a release, or modify GitHub. Generated build/cache/report files were refreshed by Gradle. The new splash has not been installed or visually tested on a TV/emulator in this pass; confirm launch/fade behavior on the Xiaomi TV and other target firmware before release. Existing real-device results describe the app before this targeted splash addition.

## 21. Splash Artwork Timing Adjustment

The user confirmed that the splash implementation works on the Xiaomi TV but requested more time to recognize/read the approved artwork. Both existing branches in `StartupSplash.kt` now use `setStartDelay(1500L)` before the unchanged 180 ms alpha fade. This is scheduled through the existing artwork pre-draw/next-frame callback, so the delay applies to the visible full-screen artwork, not an extra blank or icon-only system splash. At the normal Android animator scale, the artwork holds for approximately 1.5 seconds and then fades for 180 ms (approximately 1.68 seconds total). System animation-scale settings can affect animation timing.

The Android 12+ exit-listener architecture and Android 8–11 overlay fallback are unchanged. The approved image, FIT_CENTER scaling, navy background, removal callbacks, and focus behavior remain unchanged. Only timing and directly associated comments were changed; section 20's no-hold timing description is superseded by this adjustment. No other app functionality, resource, manifest, dependency, or package/version/SDK setting changed.

Validation command:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:compileDebugSources :app:lintDebug :app:processDebugManifest --console=plain
```

Result: **BUILD SUCCESSFUL**, 2 seconds; lint: zero errors and the same seven existing warnings. Both branches were checked for identical 1500 ms start delay and 180 ms fade. Pre/post file hashes confirmed the scope. The merged manifest still requests only the existing AndroidX app-private signature permission; no new permission or dependency was added. This pass did not package/sign an APK, publish, create a release, or modify GitHub. The adjusted duration has not been retested on the physical TV by Codex.

Exactly two maintained files were modified:

- `app/src/main/java/com/secreto/tvscreentest/StartupSplash.kt`
- `SECRETO_TV_SCREEN_TEST_CODEX_AUDIT.md`

No maintained files were created. Gradle refreshed generated build/cache/report outputs only.

## 22. Branded First-Frame Startup Fix — Final Validation

### Root cause and scope

The approved full-screen artwork is attached by app code only after activity startup (API 26–30), or when the system splash exits after app content is ready (API 31+). Before that point, Android displays the manifest-selected launch theme. The old API 26–30 launch theme inherited a solid navy `windowBackground`, with no logo, so process initialization could visibly occupy an unbranded dark window. Changing the artwork's later 1.5-second delay could not fix that earlier interval.

API 31+ already referenced the launcher icon, but did not use a dedicated safe-area branded wordmark. API 33+ also lacked `windowSplashScreenBehavior=icon_preferred`; the platform's default launch policy can choose an iconless splash. The prior exit callback hid the icon before preparing the full artwork. The fix addresses all these startup configuration/handoff gaps. The Xiaomi OS version and a startup trace were not provided, so the exact physical-device branch and cause of the measured approximately two-second initialization time have not been independently established. This fixes the content of the starting window; it does not claim to eliminate Android process-start latency.

### Implementation already present and preserved

- `splash_starting_window.xml` draws navy plus a centered 288 dp logo before app code can render. It is the launch theme's `windowBackground` for API 26–30 and remains available in inherited launch configuration.
- `splash_logo.webp` is a dedicated transparent TV/color-bars logo with outlined “SECRETO TV / SCREEN TEST” branding, derived from existing branding. It is 1152 × 1152 at xxxhdpi, equivalent to 288 dp, and only 8,700 bytes. The previous asset validation measured a maximum visible radius of 86.42 dp, within Android's 96 dp safe radius. The full 16:9 artwork is not placed into the masked system icon slot.
- API 31+ uses this logo through `windowSplashScreenAnimatedIcon`, with the existing navy `windowSplashScreenBackground`. API 33+ additionally requests `icon_preferred`.
- `StartupSplash.kt` keeps the system icon visible until the full artwork's pre-draw callback, then performs the existing handoff. Both branches retain `FIT_CENTER`, navy, the 1500 ms artwork hold, and the 180 ms fade. No timing changes were made in this first-frame correction.
- The approved full-screen WebP/source PNG, MainActivity, manifest, navigation/focus logic, existing screens/tools, localization, versions/SDKs, and dependencies remain unchanged from the start of this correction.

Android behavior reference: [official splash-screen guidance](https://developer.android.com/develop/ui/views/launch/splash-screen), including icon safe areas and API 33+ `icon_preferred` behavior.

### Final startup sequence

**Android 12+ (API 31+):** Android's system starting window displays the dedicated branded logo on navy during process initialization. API 33+ explicitly prefers the icon style. Once app content is ready, the existing exit listener attaches the approved full-screen artwork and hides the system icon when that artwork is ready to draw. The artwork holds for approximately 1.5 seconds, then fades for approximately 180 ms to the existing Home screen. The hold applies to artwork, not an added blank/system-only window.

**Android 8–11 (API 26–30):** Android's legacy starting window displays the navy-and-logo layer-list while the process starts. The existing non-focusable artwork overlay takes over when the activity draws, holds for approximately 1.5 seconds, and fades for 180 ms before removal, revealing the unchanged Home screen. No additional activity, navigation state, or focus target was introduced.

These timings assume normal system animator scale. Actual launch scheduling, system splash policy, and vendor firmware still require physical-device confirmation. Hot resumes/restored activities retain the prior behavior; this does not force a new splash on every resume.

### Final validation after interrupted work resumed

Inspected the current project before proceeding. The implementation from the interrupted task was preserved; no additional source/resource changes were made during completion because validation found no errors. Only this audit section was added during the resumed pass.

Exact command:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:compileDebugSources :app:lintDebug :app:processDebugManifest --console=plain
```

Result: **BUILD SUCCESSFUL**, 4 seconds; 27 actionable tasks (5 executed, 22 up-to-date). This is an incremental compilation/resource-linking/lint verification, not an APK packaging or clean-build claim. Lint: **0 errors, 8 warnings**. Seven warnings were already present. The additional non-blocking `IconDensities` warning notes that the splash logo has a xxxhdpi asset but no xhdpi copy; Android can scale the supplied density-qualified resource. It was deliberately left unchanged under the instruction not to do additional cleanup. No warning was suppressed.

Checked launch-theme inheritance for API 26, 30, 31, 32, 33, and 36; checked both hold/fade values; compared maintained-file hashes against the pre-correction baseline. Gradle configuration and dependency catalog match the baseline. The merged debug manifest still requests only `com.secreto.tvscreentest.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, the existing AndroidX signature permission. There is no Internet permission or newly added runtime permission, service, or dependency.

**READY FOR ANOTHER XIAOMI REAL-DEVICE SPLASH TEST.** This correction was not installed or visually retested on the physical TV by Codex. Confirm that cold launch shows the logo during process startup, then the complete artwork for approximately 1.5 seconds, followed by the unchanged fade/Home focus. No APK was packaged or signed; nothing was published/released or sent to GitHub.

### Exact maintained-file changes for the complete first-frame correction

Created:

- `app/src/main/res/drawable/splash_starting_window.xml`
- `app/src/main/res/drawable-xxxhdpi/splash_logo.webp`
- `app/src/main/res/values-v33/themes.xml`

Modified:

- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-v31/themes.xml`
- `app/src/main/java/com/secreto/tvscreentest/StartupSplash.kt`
- `SECRETO_TV_SCREEN_TEST_CODEX_AUDIT.md`

The temporary vector used to generate the optimized logo was not retained as a project resource. Gradle refreshed generated build/cache/report outputs; these are not additional maintained source changes.
