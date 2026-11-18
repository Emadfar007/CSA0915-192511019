# A1 Record Layouts and Failure Handling

## Delimited text layout

Encoding: UTF-8. Header identifies the schema. One record is stored per line.

```text
#BUSPASS|1
pass_id|register_number|name|route_number|boarding_stop|validity_date|amount_paise|crc32
P1003|192511019|Emad Farooqui|R12|Central Station|2027-05-31|125000|8BC194D2
```

Rules:

- `pass_id`: `P` followed by four or more digits; unique.
- `register_number`: exactly nine digits.
- Text fields are trimmed and must not contain `|`, CR or LF. A production format may escape them, but rejecting them keeps parsing unambiguous.
- `validity_date`: ISO-8601 `YYYY-MM-DD`.
- Money is an integer number of paise, avoiding binary floating-point errors.
- `crc32` covers the UTF-8 bytes before the final delimiter and detects accidental damage.
- Exactly eight fields are required. Blank lines are ignored; malformed records are logged with their line number.

Text files are human-readable and portable. A full scan is O(n) time; appending a validated record is O(1) amortised.

## Binary layout using DataOutputStream

File header:

| Field | Type | Bytes | Purpose |
|---|---|---:|---|
| Magic | `int` | 4 | `0x42505335` (`BPS5`) |
| Version | `short` | 2 | schema version 1 |
| Record count | `int` | 4 | number of committed records |

Each variable-length record:

| Field | DataOutputStream operation | Size |
|---|---|---:|
| Payload length | `writeInt` | 4 bytes |
| Pass ID | `writeUTF` | 2-byte length + modified UTF-8 |
| Register number | `writeUTF` | variable |
| Name | `writeUTF` | variable |
| Route number | `writeUTF` | variable |
| Boarding stop | `writeUTF` | variable |
| Validity date | `writeLong` | 8-byte epoch day |
| Amount | `writeLong` | 8-byte paise value |
| CRC32 | `writeInt` | 4 bytes |

The payload is first encoded into a byte array so its length and CRC are known. A reader refuses impossible lengths, truncated payloads or CRC mismatches. This format is compact but not directly editable and remains sequential unless an index is added.

## Missing, damaged and incomplete data

- Missing file on `find` or `list`: return a typed `STORE_NOT_FOUND` result and explain the expected path; do not silently create it.
- Missing file on the first `add`: create parent directories and a new versioned file.
- Damaged text line: catch record-level parse errors, log line number and reason, skip only that record, and continue.
- Incomplete append/power failure: write the new complete file to `passes.tmp`, flush and force it to stable storage, then atomically replace `passes.txt`. On startup, validate both files and keep the newest complete file. Never treat an unterminated or checksum-failing last line as committed.

## Comparison with fixed-length RandomAccessFile

| Property | Delimited text | Fixed-length RandomAccessFile |
|---|---|---|
| Find without index | O(n) | O(n) |
| Find by known record number | not supported | O(1) seek |
| Variable names/stops | natural | padding/truncation or overflow area |
| Human inspection | easy | difficult |
| Update in place | awkward | O(1) when width is unchanged |
| Space | separators + digits | predictable but padded |
| Schema evolution | header/version and flexible parser | difficult because offsets change |

For 8,000 annual records, validated delimited text is reasonable for exchange and audit. Fixed-length random access is useful only when stable field widths and direct positional updates are dominant requirements.
