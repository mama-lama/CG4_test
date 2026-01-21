# CG_Task4

## Build and run

### IDE run (JavaFX plugin)
```bash
mvn javafx:run
```

### Portable build
```powershell
.\scripts\package.ps1
```

The portable bundle will be created in `dist/`. Run `dist/run.bat` (Windows) or `dist/run.sh` (Unix-like).

## Controls
- Mouse drag on the viewport rotates the camera around the target.
- Mouse wheel zooms in and out.
- Use the Camera panel to add/remove cameras and set the active one.
- Render toggles are in the View menu (wireframe, texture, lighting, show cameras).
