# Anubis 3D Character Standalone

A standalone Fabric client mod for Minecraft **1.21.11**, adapted from the Anubis 0.9.9 source snapshot supplied by the user. It only replaces the **local player's** model; other players are not modified.

## Controls

Use the client command:

- `/character3d` — toggle the character renderer.
- `/character3d on` — enable it.
- `/character3d off` — disable it.
- `/character3d next` — cycle through the bundled procedural designs.

## Build locally

Requires JDK 21 and Gradle 8.

```sh
gradle build
```

The output JAR is in `build/libs/`. The GitHub Actions workflow builds the mod on each push and uploads the JAR as an artifact. To use it, create a GitHub repository, upload the **contents** of this folder to the repository root, then open **Actions → Build Fabric mod**. Download the `anubis3d-standalone` artifact from a successful run.

## Notes

- The source snapshot is Anubis **0.9.9**, not the separately mentioned Beta 0.9.9.3.
- This port includes the procedural Java character designs. The source snapshot did not include the two `.anc` bundled model assets (`cj` and `neighbor`), so those entries are omitted. External GLTF import is omitted.
- Minecraft classes in the copied renderer use Fabric's intermediary names. The build is configured against the intermediary namespace.
- This is an initial source port and has **not been compiled in this environment**. GitHub Actions is configured to compile it; if it reports a mapping/API issue, share the build log and I can fix it.
- Anubis source is identified in the supplied snapshot as GPL-3.0-only. This derivative is therefore provided under GPL-3.0-only; retain attribution and the full `LICENSE` when modifying or redistributing it. The original project is attributed to 4ldenz.
