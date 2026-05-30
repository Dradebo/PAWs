# Picture Audio Widget Architecture

## Product goal
Build an Android home-screen widget for local media that can:
- show a picture preview
- open the picture
- open/play the paired audio
- skip to the next item
- switch sort order between random, size, and recency

The first distribution path is a GitHub Release APK that can be sideloaded onto target devices.

## Feasibility summary
This is feasible with a native Android implementation.

What is straightforward:
- home-screen widget with button actions
- local image/audio indexing through MediaStore
- per-widget saved sort mode and current item
- CI-built APK artifacts attached to GitHub Releases

What needs careful handling:
- Android widget UI is constrained to RemoteViews interactions
- storage permissions differ across Android versions
- image/audio pairing rules must be deterministic and explainable
- local builds are blocked in the current workspace because the Android toolchain is missing

## v1 scope
- single widget type
- single current item at a time
- four controls: Image, Audio, Next, Sort
- sort modes: Random, Size, Recent
- simple pairing strategy for local image/audio items
- per-widget state persistence
- debug APK release assets for manual testing

## Non-goals for v1
- advanced playlist management
- lock-screen/media notification polish
- previous/back navigation
- cloud sync
- editable widget collections
- fully signed production release pipeline

## Architecture

### App layers
1. ui
   - app entry activity
   - widget configuration activity
   - future image/audio detail screens

2. widget
   - AppWidgetProvider
   - widget action broadcast receiver
   - widget renderer / updater

3. media
   - MediaStore queries
   - pairing logic
   - sorting logic
   - next-item selection rules

4. data
   - Room database
   - per-widget preferences and current selection

5. player
   - Media3 playback service/session
   - audio playback entry points

## Key domain objects

### MediaAsset
Represents a raw image or raw audio record loaded from MediaStore.

Fields:
- id
- uri
- title
- bucketName
- sizeBytes
- modifiedAtEpochMillis
- normalizedBaseName
- mimeType

### WidgetMediaItem
Represents the widget-facing merged item.

Fields:
- id
- displayTitle
- imageUri
- audioUri
- sizeBytes
- modifiedAtEpochMillis
- bucketName
- pairingConfidence

### WidgetState
Per-widget persisted state.

Fields:
- appWidgetId
- sortMode
- currentMediaItemId
- randomSeed
- selectedBucket
- updatedAtEpochMillis

## Pairing strategy
v1 pairing should be deterministic and simple.

Priority order:
1. exact normalized basename match in the same bucket/folder
2. exact normalized basename match anywhere
3. fallback unmatched image-only item
4. fallback unmatched audio-only item

Examples:
- `IMG_1001.jpg` + `IMG_1001.mp3` => exact pair
- `cover.png` + `cover.m4a` in same folder => exact pair
- no match => standalone image item or standalone audio item

Normalization rules:
- lowercase
- strip extension
- trim whitespace
- collapse repeated separators

## Sorting rules
- Random: seeded shuffle, stable for the same seed, refreshed intentionally
- Size: descending by combined size where possible
- Recent: descending by latest modification timestamp

## Widget behavior
- Image button: open image detail activity or external viewer intent
- Audio button: open player activity and start playback service
- Next button: advance to next item under current sorted list
- Sort button: cycle Random -> Size -> Recent -> Random and refresh widget

## Testing strategy

### Unit tests first
Focus on testable pure logic before Android framework wiring:
- sort mode cycling
- pairing behavior
- next-item selection
- widget state reducers / helpers

### CI verification
Because local Android build tools are unavailable here, CI is the source of truth for:
- unit tests
- APK assembly
- release asset generation

## Release strategy
- push branch updates to build debug APK artifacts
- create tags like `v0.1.0-alpha.1` to attach APKs to GitHub Releases
- use debug APKs first for device testing
- later add signing secrets for release builds

## Current environment constraints
Current workspace limitations:
- Java not installed
- Gradle not installed locally
- Android SDK not configured
- adb and sdkmanager unavailable

Result:
- code and docs can be authored here
- local APK builds cannot be verified here
- GitHub Actions must handle the first build/test loop
