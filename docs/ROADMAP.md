# Music implementation roadmap

- [x] Create Android/Kotlin/Compose project foundation.
- [x] Add initial navigation shell: Home, Library, Videos, Playlists, Profile.
- [x] Add selectable light/dark visual modes.
- [x] Present guest mode as the initial account experience.
- [x] Persist appearance and data-saver preferences with DataStore.
- [x] Add local Room database for tracks, favorites and playlists.
- [ ] Add remote catalog domain models and version-aware local catalog mapping.
- [ ] Implement version-aware catalog synchronization.
- [x] Integrate Media3 native playback for user-selected local audio files.
- [ ] Add image loading and bounded cache.
- [x] Add optional Supabase email/password authentication with guest mode preserved.
- [x] Load and refresh Supabase Auth sessions on Android.
- [x] Create Music-only account profiles without touching Interaccion profiles.
- [x] Add a read-only Supabase catalog connection check.
- [ ] Add collaborative playlists with RLS.
- [ ] Add Android share intents and official YouTube player handoff.
- [x] Persist data-saver preference (UI controls and cache limits remain pending).
- [ ] Test offline behavior, local file playback and remote-service failure behavior.
- [ ] Confirm the new GitHub Actions Android compile workflow passes.
- [ ] Test registration, email confirmation, sign-in, session restoration and sign-out on a real Android device.

- [x] Reuse the existing Interaccion Supabase project without changing its existing tables.
- [x] Apply Music schema and security migrations `0001` through `0004`.
- [ ] Create Firebase FCM project/app registration (no Firebase Auth or Firestore required).
- [ ] Add FCM token lifecycle, notification channels and Android 13+ permission handling.
- [ ] Deploy Supabase push Edge Function, configure secrets and Database Webhook.
- [ ] Add notification preferences, in-app inbox, and deep-link routing.
