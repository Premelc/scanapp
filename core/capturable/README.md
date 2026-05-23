# core:scan

Compose Multiplatform scanning component used by `feature:scan`.

## Public API

- `CodeScanner(...)`
- `ScannedCode`
- `CodeKind`
- `CodeFormat`
- `ScanError`

## Format Mapping

- `CodeKind.QR`:
  - `QR_CODE`
  - `AZTEC`
  - `DATA_MATRIX`
  - `PDF_417`
- `CodeKind.BARCODE`:
  - `CODE_39`
  - `CODE_93`
  - `CODE_128`
  - `EAN_8`
  - `EAN_13`
  - `UPC_A`
  - `UPC_E`
  - `ITF`
  - `CODABAR`
  - `UNKNOWN`

## Platform Support

- Android: CameraX + ML Kit barcode scanning.
- iOS: placeholder preview implementation in this iteration (compilation-ready).
