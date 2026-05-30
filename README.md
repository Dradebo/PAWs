# Picture Audio Widget

Android home-screen widget for local pictures and audio.

Implemented app pieces
- local MediaStore image/audio catalog loading
- deterministic filename normalization and pairing
- sortable widget catalog: random, size, recency
- per-widget Room-backed state persistence
- widget actions: open image, open audio, next, sort
- main app screen for permission grant and widget refresh
- widget configuration activity
- in-app audio playback activity using Media3 ExoPlayer

Unit-tested logical modules
- media: name normalization, pairing, sorting
- data: widget state repository behavior
- widget: navigation, state reduction, coordinator flow, view-state mapping
- permissions: runtime permission policy by SDK level
- player: audio launch contract
- ui: main screen state mapping

Build/test path
- local Android builds are not available in this environment because Java/Gradle/Android SDK are missing
- the intended final validation is GitHub Actions:
  - run unit tests
  - build debug APK
  - upload APK artifact
  - attach APK to tagged GitHub Releases

Release flow
1. Push this project to a GitHub repository.
2. Let the workflow run on push/PR.
3. Create a tag like `v0.1.0-alpha.1`.
4. Download the release APK and test on target Android devices.
