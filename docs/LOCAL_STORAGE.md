# Local storage and offline library

Music now has a Room-backed local library for tracks, favorites, playlists and playlist membership. The guest experience is local-first: these records stay on the device and are not uploaded automatically.

Preferences are stored with DataStore (light/dark appearance and data-saver preference).

## Media ownership and safety

- A local track can reference a user-selected device URI or an authorized media URI.
- A catalog record is metadata, not a downloaded media file.
- YouTube records must continue to use the official YouTube player/embed flow. The app must not extract, download, cache or play YouTube media in the background.
- The database stores metadata and URI references; it does not copy media files into app storage.

## Current scope

The Room schema and repository are in place. The visible library screens still need to be wired to these flows, and catalog synchronization needs a version-aware merge strategy before it is enabled. Never replace a local file URI with a remote catalog URL during sync.
