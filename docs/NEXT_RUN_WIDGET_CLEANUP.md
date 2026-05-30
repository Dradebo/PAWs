# Next Run Brief: Widget Load Failure + Folder Selection Cleanup

Date captured: 2026-05-30
Current release tested: `v0.2.0-alpha.1`
Screenshots reviewed:
- `docs/Screenshot_20260530-180157.png` — widget folder selection screen
- `docs/Screenshot_20260530-180218.png` — Android home screen showing `Can't load widget`

## User feedback to preserve

Sean expects:
- The image to take up the whole widget.
- Icon buttons inside the widget, bottom-right, with their own contrast handling.
- Source folder selection to be neat and usable, not a giant flat list.
- Main app to feel more complete/polished and make selection work better.

Current feedback on `v0.2.0-alpha.1`:
- Widget does not load; launcher shows `Can't load widget`.
- Folder selection is extremely messy.
- Folder selection should use a neater tree structure.
- Main app is still bland.
- Selection needs to work better before more screenshot review.

## Screenshot assessment

### 1. Folder selection screen

Observed state:
- Header: `Choose widget folders`.
- Helper copy is understandable.
- Shows `291 folders found`.
- List is flat and overwhelming.
- Entries are long raw MediaStore paths like:
  - `Download/sound pack/LOOPMASTERS+4GB+Pack/.../IB_WAV_LOOPS/IB_BASS_LOOPS`
- Each row repeats most of the same parent path.
- Checkbox is far right, disconnected from the folder label.
- Row height becomes huge due wrapped path text.
- Save button is visible but grey-looking and visually weak.

UX problems:
- A flat list of hundreds of folders is not usable.
- Long sample-pack paths dominate the screen and bury meaningful folder names.
- User cannot quickly select a parent folder and include subfolders.
- User cannot collapse/expand tree branches.
- User cannot search/filter.
- There is no indication of item counts per folder.
- There are no quick presets like Camera, Downloads, WhatsApp, Screenshots, Music.
- There is no selected-summary area/chips.
- It is unclear whether selecting a leaf folder includes media only in that folder or also children.

Required direction:
- Replace flat folder list with a hierarchical tree model.
- Build tree from MediaStore `RELATIVE_PATH` / bucket paths.
- Show collapsed top-level folders first, e.g.:
  - Camera
  - DCIM
  - Download
  - Music
  - Pictures
  - WhatsApp
- Allow expanding folders to reveal children.
- Selecting a parent should include all descendants unless user drills down and customizes.
- Show counts next to nodes, e.g. `Download (1,284)` and `Bass Loops (42)`.
- Add search/filter for folder names.
- Add selected chips/summary: `3 folders selected`.
- Consider hiding or grouping extremely deep sample-pack paths until expanded.

### 2. Home screen widget

Observed state:
- Launcher shows a rounded black widget placeholder with centered text: `Can't load widget`.
- The full-bleed layout did not render at all.

Likely root causes to investigate first:
1. RemoteViews inflation error caused by unsupported widget XML view/class.
   - `widget_picture_audio.xml` currently includes a raw `<View>` for bottom scrim. AppWidget `RemoteViews` only supports a restricted set of view classes. Replace raw `View` with a supported widget-compatible element, likely a `TextView` or `ImageView` with background.
2. Some layout attribute or drawable may be unsupported by the launcher widget host.
3. Text glyph controls are not the likely cause, but replace with simple supported ImageView/TextView controls if necessary.
4. Need inspect device logs with `adb logcat` if available on target device; search for `RemoteViews`, `InflateException`, `AppWidgetHostView`, package `com.sean.pictureaudiowidget`.

Important: The widget-load bug takes priority over visual polish. Do not cut another screenshot release until the launcher renders the widget.

## Next implementation priorities

### Priority 1 — Fix widget rendering

Goal:
- The launcher should render the widget instead of `Can't load widget`.

Steps:
1. Audit `app/src/main/res/layout/widget_picture_audio.xml` against RemoteViews supported classes.
2. Remove/replace unsupported `<View>` scrim.
3. Keep root and children simple:
   - `FrameLayout`
   - `ImageView`
   - `TextView`
   - `LinearLayout`
4. Consider switching to a safer layout if launcher compatibility is shaky:
   - root `FrameLayout`
   - image `ImageView match_parent`
   - bottom scrim as `TextView` with gradient/solid background
   - bottom-right `LinearLayout` with `TextView` icon controls
5. Add a widget XML compatibility note/test checklist to docs.
6. Cut a quick CI build only after a static review confirms no unsupported RemoteViews classes.

Acceptance:
- Home screen no longer says `Can't load widget`.
- Widget displays either:
  - selected media image full-bleed, or
  - a clear full-bleed empty/config state.

### Priority 2 — Replace flat folder picker with tree selector

Goal:
- Folder selection should be clean enough to use with hundreds of folders.

Data model:
- Add a pure Kotlin tree model, e.g. `SourceFolderNode`:
  - `path: String`
  - `name: String`
  - `children: List<SourceFolderNode>`
  - `directItemCount: Int`
  - `totalItemCount: Int`
  - `selectedState: Selected / Partial / Unselected`
  - `expanded: Boolean`
- Build from MediaStore bucket/relative paths.
- Unit-test tree building and selection propagation.

UI direction:
- Use a proper Activity UI, preferably RecyclerView if adding dependency is acceptable.
- Minimum viable tree UI:
  - top-level collapsed folders
  - indentation per level
  - expand/collapse chevron
  - checkbox per node
  - item count badge
  - search field
  - selected-summary chips or `N folders selected`
- Selecting parent includes descendants.
- Partial selection is visually indicated.
- Provide quick actions:
  - Select Camera/DCIM
  - Select Downloads
  - Clear all
  - Select visible

Copy:
- Replace `291 folders found. Select at least one.` with something like:
  - `Choose the folders this widget should browse.`
  - `Select a parent folder to include its subfolders.`
  - `291 folders indexed • 0 selected`

Acceptance:
- User can select useful source folders without scrolling through raw long paths.
- Deep paths are discoverable but not forced onto the first screen.
- The final selected sources are obvious before saving.

### Priority 3 — Main app polish

Current main app issue:
- It is still mostly a bland permission/status utility.

Direction:
- Treat the main app as the control center for PAWs.
- Sections:
  1. Hero/status:
     - PAWs
     - `Picture, audio, and widget source control`
  2. Permissions card:
     - granted/missing state
  3. Library card:
     - media counts by type and source count
     - last refresh time if tracked
  4. Source folders card:
     - `Configure widget folders when adding a widget`
     - maybe global folder browser preview
  5. Widget help card:
     - `Add widget → choose folders → use overlay controls`
- Make buttons clearer:
  - `Grant media access`
  - `Refresh widgets`
  - `Open folder browser` / `Preview folders` if implemented

Acceptance:
- App explains the workflow.
- App does not just show a giant indexed count.
- App feels intentionally designed, not a placeholder.

## Technical notes from current implementation

Current widget release commit:
- `99a4cc1 fix: escape source bucket delimiter`

Current release APK:
- `v0.2.0-alpha.1`
- CI succeeded, but device screenshot shows runtime widget host failure.

Files likely involved next run:
- `app/src/main/res/layout/widget_picture_audio.xml`
- `app/src/main/res/drawable/widget_scrim_bottom.xml`
- `app/src/main/java/com/sean/pictureaudiowidget/widget/PictureAudioWidgetProvider.kt`
- `app/src/main/java/com/sean/pictureaudiowidget/ui/WidgetConfigActivity.kt`
- `app/src/main/java/com/sean/pictureaudiowidget/media/MediaCatalogRepository.kt`
- `app/src/main/java/com/sean/pictureaudiowidget/media/MediaStoreCatalogRepository.kt`
- `app/src/main/java/com/sean/pictureaudiowidget/data/WidgetStateEntity.kt`
- `app/src/test/java/com/sean/pictureaudiowidget/widget/*`

## Suggested next-run plan

1. Start with widget load failure.
   - Do not redesign first.
   - Make RemoteViews XML maximally conservative.
   - Cut `v0.2.0-alpha.2` only after CI passes.
   - Ask Sean for a screenshot to confirm the widget renders.

2. Then implement tree folder selection.
   - Add pure tests for folder tree building and selection state.
   - Replace `ListView` flat list with tree UI.
   - Cut `v0.2.0-alpha.3` for screenshot review.

3. Then polish main app.
   - Control-center design, library/source summaries, better buttons.
   - Cut `v0.2.0-alpha.4` if needed.

## Do not repeat

- Do not show raw long folder paths as the primary list label.
- Do not release another widget layout without checking RemoteViews-supported classes.
- Do not rely on CI alone for widget runtime correctness; CI can pass while launcher inflation fails.
- Do not make folder source selection a flat list again.
