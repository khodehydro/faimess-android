"""Launcher icon layers for FAIMESS Android — cut from the brand mark, not redrawn.

The Android app shows the same artwork as the FAIMESS web app: the white
cat on the brand purple, one raster file. The web repository keeps it at
`src/assets/brand/faimess-logo.png` (150×150); this repository carries a
byte-identical copy at

    app/src/main/res/drawable-nodpi/brand_logo.png   ← the mark itself

which the in-app header draws directly (clipped to the same 32.5% corner
the site uses). This script derives the *adaptive launcher icon* from it:

    app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher_foreground.png

The foreground is the cat alone — white with soft alpha edges on a
transparent canvas — while the background layer of the adaptive icon is
the flat brand purple (`@color/launcher_background`, `#8267F0`). Whatever
mask a launcher applies, the two layers recompose the original picture.
The same foreground is the splash-screen icon: on a purple splash window
it reads as the mark again, with no visible circle.

Why not simply scale the PNG? The artwork is flat: exactly two colours,
the field `#8267F0` and the cat `#F8F8F8`, with one pixel of anti-aliasing
between them. Scaling pixels would carry that soft border along and blur
it. Instead — mirroring `tools/appicons.py` in the web repository — the
PNG is decoded into a *coverage* field (the green channel is a clean
103→248 ramp between the two colours) and that field is re-rendered at
every target size with supersampling. The result is the mark itself, at
any resolution, with crisp edges.

Safe zone: the adaptive icon canvas is 108 dp and launchers may clip
anything outside the central 66 dp circle. The farthest point of the cat
(the ear tips) lies at 0.861 of the artwork's half-side from its centre,
so the artwork square is drawn at 74/108 of the canvas: 0.861 × 37 dp =
31.9 dp, which leaves room for the softened edge inside the 33 dp radius.
The artwork keeps its own framing (the mark sits very slightly above
centre by design); nothing is re-centred.

    python3 tools/brand_assets.py        # rewrites the five foreground PNGs

No third-party libraries: the PNG it reads and the PNGs it writes are
handled by `zlib` and `struct` alone, so a bare python3 is enough.
"""

import pathlib
import struct
import zlib

ROOT = pathlib.Path(__file__).resolve().parent.parent
RES = ROOT / "app/src/main/res"
SOURCE = RES / "drawable-nodpi/brand_logo.png"

# the two colours of the artwork; the first is `Primary` in ui/theme/Color.kt
# and `@color/brand_primary` in res/values/colors.xml
FIELD = (0x82, 0x67, 0xF0)
MARK = (0xF8, 0xF8, 0xF8)
FIELD_G, MARK_G = FIELD[1], MARK[1]

# adaptive icon geometry (dp): full canvas, and the side of the artwork square
CANVAS_DP = 108
ARTWORK_DP = 74

DENSITIES = (("mdpi", 1.0), ("hdpi", 1.5), ("xhdpi", 2.0), ("xxhdpi", 3.0), ("xxxhdpi", 4.0))


# ------------------------------------------------------------------ #
#  PNG in / PNG out — standard library only
# ------------------------------------------------------------------ #

def decode(path):
    """Read an 8-bit RGBA PNG into `(width, height, rows)` of RGBA bytes."""
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise SystemExit(f"{path}: not a PNG")
    pos, idat, head = 8, b"", None
    while pos < len(data):
        length = struct.unpack(">I", data[pos:pos + 4])[0]
        tag = data[pos + 4:pos + 8]
        body = data[pos + 8:pos + 8 + length]
        if tag == b"IHDR":
            head = struct.unpack(">IIBBBBB", body)
        elif tag == b"IDAT":
            idat += body
        pos += 12 + length
    width, height, depth, colour, _, _, interlace = head
    if (depth, colour, interlace) != (8, 6, 0):
        raise SystemExit(f"{path}: expected an 8-bit non-interlaced RGBA PNG")

    raw = zlib.decompress(idat)
    stride = width * 4
    rows, prev, at = [], bytearray(stride), 0
    for _ in range(height):
        kind = raw[at]
        at += 1
        line = bytearray(raw[at:at + stride])
        at += stride
        if kind == 1:
            for i in range(4, stride):
                line[i] = (line[i] + line[i - 4]) & 255
        elif kind == 2:
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 255
        elif kind == 3:
            for i in range(stride):
                a = line[i - 4] if i >= 4 else 0
                line[i] = (line[i] + ((a + prev[i]) >> 1)) & 255
        elif kind == 4:
            for i in range(stride):
                a = line[i - 4] if i >= 4 else 0
                b = prev[i]
                c = prev[i - 4] if i >= 4 else 0
                pa, pb, pc = abs(b - c), abs(a - c), abs(a + b - 2 * c)
                near = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[i] = (line[i] + near) & 255
        rows.append(bytes(line))
        prev = line
    return width, height, rows


def encode(size, rgba):
    def chunk(tag, body):
        return (struct.pack(">I", len(body)) + tag + body
                + struct.pack(">I", zlib.crc32(tag + body) & 0xFFFFFFFF))

    raw = b"".join(b"\x00" + rgba[y * size * 4:(y + 1) * size * 4] for y in range(size))
    return (b"\x89PNG\r\n\x1a\n"
            + chunk(b"IHDR", struct.pack(">IIBBBBB", size, size, 8, 6, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(raw, 9))
            + chunk(b"IEND", b""))


# ------------------------------------------------------------------ #
#  The coverage field — one float per source pixel
# ------------------------------------------------------------------ #

def coverage(rows, width):
    """`0.0` = the purple field, `1.0` = the white mark, in between = an edge.

    Alpha is ignored on purpose: the only transparency in the source is a
    one-pixel border that is already field-coloured.
    """
    span = MARK_G - FIELD_G

    def level(g):
        # the cat's interior is #F6–#F8 rather than one exact value; snap the
        # last two percent of the ramp so the body is fully opaque
        return min(1.0, max(0.0, ((g - FIELD_G) / span - 0.02) / 0.96))

    return [level(row[i * 4 + 1]) for row in rows for i in range(width)]


def smooth(field, width, height):
    """One 3×3 binomial pass: widens the one-pixel edge to ~2px so the
    supersampler renders a soft ramp instead of the source's stairs."""
    kern = (1, 2, 1, 2, 4, 2, 1, 2, 1)
    out = []
    for y in range(height):
        for x in range(width):
            acc = tot = 0
            for dy in (-1, 0, 1):
                yy = min(max(y + dy, 0), height - 1)
                for dx in (-1, 0, 1):
                    xx = min(max(x + dx, 0), width - 1)
                    k = kern[(dy + 1) * 3 + (dx + 1)]
                    acc += field[yy * width + xx] * k
                    tot += k
            out.append(acc / tot)
    return out


def catmull(p0, p1, p2, p3, t):
    return 0.5 * (2 * p1 + (-p0 + p2) * t
                  + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t
                  + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t)


def sample(field, width, height, x, y):
    """Catmull-Rom lookup on the field, clamped at the borders; the result
    is clipped so overshoot can only round an edge, never tint it."""
    x = min(max(x, 0.0), width - 1.0)
    y = min(max(y, 0.0), height - 1.0)
    xi, yi = int(x), int(y)
    fx, fy = x - xi, y - yi

    def at(i, j):
        j = min(max(j, 0), height - 1)
        i = min(max(i, 0), width - 1)
        return field[j * width + i]

    columns = [catmull(at(xi - 1, yi + k), at(xi, yi + k),
                       at(xi + 1, yi + k), at(xi + 2, yi + k), fx)
               for k in (-1, 0, 1, 2)]
    return min(1.0, max(0.0, catmull(columns[0], columns[1], columns[2], columns[3], fy)))


def render_foreground(field, width, height, size, artwork_frac):
    """The cat alone on a transparent `size`×`size` canvas.

    The artwork square is centred and scaled to `artwork_frac` of the
    canvas; every output pixel is supersampled (3×3, or 4×4 at small
    sizes) and gets the mark colour with alpha = coverage. Outside the
    artwork square coverage is zero by definition (that is the purple
    field in the source), so nothing but the cat is ever painted.
    """
    steps = 4 if size <= 128 else 3
    step = 1.0 / steps
    inset = (1.0 - artwork_frac) / 2
    pixels = bytearray()
    for y in range(size):
        for x in range(size):
            light = 0.0
            for sy in range(steps):
                for sx in range(steps):
                    u = ((x + (sx + 0.5) * step) / size - inset) / artwork_frac
                    v = ((y + (sy + 0.5) * step) / size - inset) / artwork_frac
                    if 0.0 <= u < 1.0 and 0.0 <= v < 1.0:
                        light += sample(field, width, height, u * width - 0.5, v * height - 0.5)
            alpha = light / (steps * steps)
            pixels += bytes(MARK)
            pixels.append(round(255 * alpha))
    return bytes(pixels)


# ------------------------------------------------------------------ #

def main():
    width, height, rows = decode(SOURCE)
    field = smooth(coverage(rows, width), width, height)
    print(f"source  {SOURCE.relative_to(ROOT)}  {width}×{height}")
    artwork_frac = ARTWORK_DP / CANVAS_DP
    for name, scale in DENSITIES:
        size = round(CANVAS_DP * scale)
        folder = RES / f"mipmap-{name}"
        folder.mkdir(parents=True, exist_ok=True)
        target = folder / "ic_launcher_foreground.png"
        data = encode(size, render_foreground(field, width, height, size, artwork_frac))
        target.write_bytes(data)
        print(f"  → {target.relative_to(ROOT)}  {size}×{size}  {len(data) / 1024:6.2f} KB")


if __name__ == "__main__":
    main()
