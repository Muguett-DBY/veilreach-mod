Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.IO.Compression.FileSystem

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$assetsRoot = Join-Path $root "src\main\resources\assets\veilreach"
$blockDir = Join-Path $assetsRoot "textures\block"
$itemDir = Join-Path $assetsRoot "textures\item"
$entityDir = Join-Path $assetsRoot "textures\entity"
$iconPath = Join-Path $assetsRoot "icon.png"
$minecraftZipPath = "E:\MOD\minecraft-merged.zip"
$tileSize = 64
$entitySheetSize = 64

foreach ($dir in @($blockDir, $itemDir, $entityDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir | Out-Null
    }
}

$random = [System.Random]::new(1337)

function Color-Hex([string]$hex) {
    return [System.Drawing.ColorTranslator]::FromHtml($hex)
}

function Clamp-Byte([double]$value) {
    return [Math]::Max(0, [Math]::Min(255, [int][Math]::Round($value)))
}

function Mix-Color([System.Drawing.Color]$a, [System.Drawing.Color]$b, [double]$t) {
    $t = [Math]::Max(0.0, [Math]::Min(1.0, $t))
    return [System.Drawing.Color]::FromArgb(
        (Clamp-Byte($a.A + (($b.A - $a.A) * $t))),
        (Clamp-Byte($a.R + (($b.R - $a.R) * $t))),
        (Clamp-Byte($a.G + (($b.G - $a.G) * $t))),
        (Clamp-Byte($a.B + (($b.B - $a.B) * $t)))
    )
}

function Shade-Color([System.Drawing.Color]$color, [int]$delta) {
    return [System.Drawing.Color]::FromArgb(
        $color.A,
        (Clamp-Byte($color.R + $delta)),
        (Clamp-Byte($color.G + $delta)),
        (Clamp-Byte($color.B + $delta))
    )
}

function New-Bitmap([int]$width = 16, [int]$height = 16) {
    $bmp = [System.Drawing.Bitmap]::new($width, $height)
    for ($x = 0; $x -lt $width; $x++) {
        for ($y = 0; $y -lt $height; $y++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }
    return $bmp
}

function Resize-Nearest([System.Drawing.Bitmap]$source, [int]$width, [int]$height) {
    $resized = [System.Drawing.Bitmap]::new($width, $height)
    $graphics = [System.Drawing.Graphics]::FromImage($resized)
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
    $graphics.DrawImage($source, 0, 0, $width, $height)
    $graphics.Dispose()
    return $resized
}

function Add-HiResSurfaceDetails([System.Drawing.Bitmap]$bmp, [int]$seedOffset = 0) {
    $localRandom = [System.Random]::new(1337 + $seedOffset)
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        for ($y = 0; $y -lt $bmp.Height; $y++) {
            $px = $bmp.GetPixel($x, $y)
            if ($px.A -eq 0) {
                continue
            }

            $detail = [Math]::Sin(($x + $seedOffset) * 0.37) + [Math]::Cos(($y - $seedOffset) * 0.29)
            $grain = $localRandom.Next(-8, 9)
            $delta = [int]([Math]::Round(($detail * 5.0) + $grain))
            $enhanced = Shade-Color $px $delta

            if ((($x + $y + $seedOffset) % 13) -eq 0) {
                $enhanced = Mix-Color $enhanced ([System.Drawing.Color]::White) 0.08
            } elseif ((($x * 3 + $y + $seedOffset) % 17) -eq 0) {
                $enhanced = Mix-Color $enhanced ([System.Drawing.Color]::Black) 0.10
            }

            $bmp.SetPixel($x, $y, ([System.Drawing.Color]::FromArgb($px.A, $enhanced.R, $enhanced.G, $enhanced.B)))
        }
    }
}

function Normalize-TextureForPath([System.Drawing.Bitmap]$bmp, [string]$path) {
    $normalized = $bmp
    if ($path -like "*\textures\block\hush_portal.png" -or $path -like "*\textures\block\starlantern.png" -or $path -like "*\textures\block\starlight_cache.png") {
        if ($bmp.Width -ne $tileSize -or $bmp.Height -ne ($tileSize * 4)) {
            $normalized = Resize-Nearest $bmp $tileSize ($tileSize * 4)
            $bmp.Dispose()
        }
        Add-HiResSurfaceDetails $normalized ([Math]::Abs($path.GetHashCode()) % 1000)
        return $normalized
    }
    if ($path -like "*\textures\block\*" -or $path -like "*\textures\item\*") {
        if ($bmp.Width -ne $tileSize -or $bmp.Height -ne $tileSize) {
            $normalized = Resize-Nearest $bmp $tileSize $tileSize
            $bmp.Dispose()
        }
        Add-HiResSurfaceDetails $normalized ([Math]::Abs($path.GetHashCode()) % 1000)
    } elseif ($path -like "*\textures\entity\*") {
        if ($bmp.Width -lt $entitySheetSize -or $bmp.Height -lt $entitySheetSize) {
            $canvas = [System.Drawing.Bitmap]::new($entitySheetSize, $entitySheetSize)
            $graphics = [System.Drawing.Graphics]::FromImage($canvas)
            $graphics.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
            $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
            $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
            $graphics.DrawImage($bmp, 0, 0, $bmp.Width, $bmp.Height)
            $graphics.Dispose()
            $bmp.Dispose()
            $normalized = $canvas
        }
        Add-HiResSurfaceDetails $normalized ([Math]::Abs($path.GetHashCode()) % 1000)
    }
    return $normalized
}

function Normalize-ExistingTextures([string]$directory) {
    Get-ChildItem -Path $directory -Filter *.png | ForEach-Object {
        $source = [System.Drawing.Bitmap]::new($_.FullName)
        $normalized = Normalize-TextureForPath $source $_.FullName
        $tempPath = "$($_.FullName).tmp"
        $normalized.Save($tempPath, [System.Drawing.Imaging.ImageFormat]::Png)
        $normalized.Dispose()
        Move-Item -Force $tempPath $_.FullName
    }
}

function Save-Png([System.Drawing.Bitmap]$bmp, [string]$path) {
    $finalBmp = Normalize-TextureForPath $bmp $path
    $finalBmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $finalBmp.Dispose()
}

function Draw-Line([System.Drawing.Bitmap]$bmp, [int]$x1, [int]$y1, [int]$x2, [int]$y2, [System.Drawing.Color]$color) {
    $dx = [Math]::Abs($x2 - $x1)
    $dy = -[Math]::Abs($y2 - $y1)
    $sx = if ($x1 -lt $x2) { 1 } else { -1 }
    $sy = if ($y1 -lt $y2) { 1 } else { -1 }
    $err = $dx + $dy

    while ($true) {
        if ($x1 -ge 0 -and $x1 -lt $bmp.Width -and $y1 -ge 0 -and $y1 -lt $bmp.Height) {
            $bmp.SetPixel($x1, $y1, $color)
        }
        if ($x1 -eq $x2 -and $y1 -eq $y2) {
            break
        }
        $e2 = 2 * $err
        if ($e2 -ge $dy) {
            $err += $dy
            $x1 += $sx
        }
        if ($e2 -le $dx) {
            $err += $dx
            $y1 += $sy
        }
    }
}

function Fill-Noise([System.Drawing.Bitmap]$bmp, [System.Drawing.Color]$dark, [System.Drawing.Color]$light, [int]$variance = 18) {
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        for ($y = 0; $y -lt $bmp.Height; $y++) {
            $mix = (($x + $y) / (($bmp.Width - 1) + ($bmp.Height - 1)))
            $base = Mix-Color $dark $light $mix
            $delta = $random.Next(-$variance, $variance + 1)
            $bmp.SetPixel($x, $y, (Shade-Color $base $delta))
        }
    }
}

function Draw-Border([System.Drawing.Bitmap]$bmp, [System.Drawing.Color]$outer, [System.Drawing.Color]$inner) {
    for ($i = 0; $i -lt $bmp.Width; $i++) {
        $bmp.SetPixel($i, 0, $outer)
        $bmp.SetPixel($i, $bmp.Height - 1, $outer)
        $bmp.SetPixel($i, 1, $inner)
        $bmp.SetPixel($i, $bmp.Height - 2, $inner)
    }
    for ($i = 0; $i -lt $bmp.Height; $i++) {
        $bmp.SetPixel(0, $i, $outer)
        $bmp.SetPixel($bmp.Width - 1, $i, $outer)
        $bmp.SetPixel(1, $i, $inner)
        $bmp.SetPixel($bmp.Width - 2, $i, $inner)
    }
}

function Add-Sparkles([System.Drawing.Bitmap]$bmp, [int]$count, [System.Drawing.Color]$color) {
    for ($i = 0; $i -lt $count; $i++) {
        $x = $random.Next(1, $bmp.Width - 1)
        $y = $random.Next(1, $bmp.Height - 1)
        $bmp.SetPixel($x, $y, $color)
        $bmp.SetPixel($x - 1, $y, (Shade-Color $color -30))
        $bmp.SetPixel($x + 1, $y, (Shade-Color $color -30))
        $bmp.SetPixel($x, $y - 1, (Shade-Color $color -30))
        $bmp.SetPixel($x, $y + 1, (Shade-Color $color -30))
    }
}

function Draw-Diamond([System.Drawing.Bitmap]$bmp, [int]$cx, [int]$cy, [int]$radius, [System.Drawing.Color]$fill, [System.Drawing.Color]$edge) {
    for ($x = -$radius; $x -le $radius; $x++) {
        for ($y = -$radius; $y -le $radius; $y++) {
            if ([Math]::Abs($x) + [Math]::Abs($y) -le $radius) {
                $px = $cx + $x
                $py = $cy + $y
                if ($px -ge 0 -and $px -lt $bmp.Width -and $py -ge 0 -and $py -lt $bmp.Height) {
                    $color = if ([Math]::Abs($x) + [Math]::Abs($y) -ge ($radius - 1)) { $edge } else { $fill }
                    $bmp.SetPixel($px, $py, $color)
                }
            }
        }
    }
}

function Draw-Ring([System.Drawing.Bitmap]$bmp, [int]$cx, [int]$cy, [int]$radius, [System.Drawing.Color]$color) {
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        for ($y = 0; $y -lt $bmp.Height; $y++) {
            $distance = [Math]::Sqrt((($x - $cx) * ($x - $cx)) + (($y - $cy) * ($y - $cy)))
            if ($distance -ge ($radius - 0.8) -and $distance -le ($radius + 0.8)) {
                $bmp.SetPixel($x, $y, $color)
            }
        }
    }
}

function Draw-Star([System.Drawing.Bitmap]$bmp, [int]$cx, [int]$cy, [System.Drawing.Color]$core, [System.Drawing.Color]$accent) {
    $bmp.SetPixel($cx, $cy, $core)
    foreach ($offset in @(@(1,0), @(-1,0), @(0,1), @(0,-1), @(1,1), @(-1,1), @(1,-1), @(-1,-1))) {
        $bmp.SetPixel($cx + $offset[0], $cy + $offset[1], $accent)
    }
}

function Draw-ToolHandle([System.Drawing.Bitmap]$bmp, [int]$x1, [int]$y1, [int]$x2, [int]$y2) {
    Draw-Line $bmp $x1 $y1 $x2 $y2 (Color-Hex "#4a2e1f")
    Draw-Line $bmp ($x1 + 1) $y1 ($x2 + 1) $y2 (Color-Hex "#805139")
}

function Create-StoneBlock([string]$path, [string]$baseDark, [string]$baseLight, [string]$vein) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex $baseDark) (Color-Hex $baseLight) 12
    for ($i = 0; $i -lt 3; $i++) {
        Draw-Line $bmp $random.Next(0, 5) $random.Next(0, 15) $random.Next(10, 15) $random.Next(0, 15) (Color-Hex $vein)
    }
    Save-Png $bmp $path
}

function Create-WoodPlanks([string]$path, [string]$base, [string]$accent) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex "#18232d") (Color-Hex $base) 10
    for ($y = 2; $y -lt 16; $y += 4) {
        Draw-Line $bmp 0 $y 15 $y (Color-Hex "#0c1319")
        Draw-Line $bmp 0 ($y + 1) 15 ($y + 1) (Color-Hex $accent)
    }
    for ($x = 1; $x -lt 15; $x += 5) {
        Draw-Line $bmp $x 0 ($x + 2) 15 (Shade-Color (Color-Hex $accent) -30)
    }
    Save-Png $bmp $path
}

function Create-WoodLog([string]$path) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex "#11161d") (Color-Hex "#355266") 14
    for ($x = 1; $x -lt 16; $x += 3) {
        Draw-Line $bmp $x 0 $x 15 (Color-Hex "#5a879f")
    }
    Draw-Line $bmp 7 0 7 15 (Color-Hex "#91d0df")
    Draw-Line $bmp 8 0 8 15 (Color-Hex "#91d0df")
    Save-Png $bmp $path
}

function Create-Leaves([string]$path) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex "#23343d") (Color-Hex "#6d9ea6") 18
    for ($x = 0; $x -lt 16; $x++) {
        for ($y = 0; $y -lt 16; $y++) {
            if ((($x + $y) % 5) -eq 0 -or $random.NextDouble() -lt 0.16) {
                $bmp.SetPixel($x, $y, ([System.Drawing.Color]::FromArgb(170, 180, 255, 236)))
            }
        }
    }
    Add-Sparkles $bmp 4 (Color-Hex "#f4fff7")
    Save-Png $bmp $path
}

function Create-Flower([string]$path, [string]$petal, [string]$core, [string]$stem) {
    $bmp = New-Bitmap
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }
    Draw-Diamond $bmp 8 6 3 (Color-Hex $petal) (Shade-Color (Color-Hex $petal) 24)
    Draw-Diamond $bmp 6 8 2 (Color-Hex $petal) (Shade-Color (Color-Hex $petal) 18)
    Draw-Diamond $bmp 10 8 2 (Color-Hex $petal) (Shade-Color (Color-Hex $petal) 18)
    Draw-Diamond $bmp 8 8 1 (Color-Hex $core) (Shade-Color (Color-Hex $core) 18)
    Draw-Line $bmp 8 9 8 15 (Color-Hex $stem)
    Draw-Line $bmp 8 12 6 14 (Shade-Color (Color-Hex $stem) 20)
    Draw-Line $bmp 8 11 10 13 (Shade-Color (Color-Hex $stem) 12)
    Save-Png $bmp $path
}

function Create-Glass([string]$path) {
    $bmp = New-Bitmap
    Fill-Noise $bmp ([System.Drawing.Color]::FromArgb(145, 18, 32, 48)) ([System.Drawing.Color]::FromArgb(165, 90, 154, 181)) 10
    Draw-Line $bmp 1 12 14 2 ([System.Drawing.Color]::FromArgb(210, 232, 247, 255))
    Draw-Line $bmp 2 4 13 9 ([System.Drawing.Color]::FromArgb(175, 160, 232, 255))
    Add-Sparkles $bmp 3 ([System.Drawing.Color]::FromArgb(220, 255, 255, 255))
    Save-Png $bmp $path
}

function Create-Portal([string]$path) {
    $frameHeight = 16
    $bmp = New-Bitmap 16 ($frameHeight * 4)
    for ($frame = 0; $frame -lt 4; $frame++) {
        $offsetY = $frame * $frameHeight
        for ($x = 0; $x -lt 16; $x++) {
            for ($y = 0; $y -lt $frameHeight; $y++) {
                $distance = [Math]::Sqrt((($x - 7.5) * ($x - 7.5)) + (($y - 7.5) * ($y - 7.5)))
                $t = [Math]::Min(1.0, $distance / 10.0)
                $base = Mix-Color ([System.Drawing.Color]::FromArgb(90, 12, 18, 34)) ([System.Drawing.Color]::FromArgb(180, 96, 171, 230)) (1.0 - $t)
                $pulse = [Math]::Sin(($x * 0.8) + ($y * 0.6) + ($frame * 0.9))
                $swirl = [Math]::Cos((($x - $y) * 0.45) + ($frame * 1.3))
                $sparkle = if ((($x + $y + ($frame * 2)) % 5) -eq 0) { 12 } else { 0 }
                $color = Shade-Color $base ([int](($pulse * 18) + ($swirl * 10) + $sparkle))
                $bmp.SetPixel($x, $offsetY + $y, $color)
            }
        }
        Draw-Ring $bmp 8 ($offsetY + 8) 5 (Color-Hex "#d7f7ff")
        Draw-Star $bmp 8 ($offsetY + 8) (Color-Hex "#fff7d9") (Color-Hex "#9bddff")
    }
    Save-Png $bmp $path
}

function Create-Lantern([string]$path) {
    $frameHeight = 16
    $bmp = New-Bitmap 16 ($frameHeight * 4)
    $glows = @("#ffd98f", "#ffefb3", "#ffe08a", "#fff2c7")
    for ($frame = 0; $frame -lt 4; $frame++) {
        $offsetY = $frame * $frameHeight
        Fill-Noise $bmp (Color-Hex "#14141c") (Color-Hex "#3a4153") 8
        for ($x = 0; $x -lt 16; $x++) {
            for ($y = 0; $y -lt $frameHeight; $y++) {
                if ($offsetY + $y -lt $bmp.Height) {
                    $current = $bmp.GetPixel($x, $offsetY + $y)
                    if ($current.A -eq 0) {
                        $bmp.SetPixel($x, $offsetY + $y, (Color-Hex "#14141c"))
                    }
                }
            }
        }
        for ($x = 0; $x -lt 16; $x++) {
            $bmp.SetPixel($x, $offsetY, (Color-Hex "#0d1118"))
            $bmp.SetPixel($x, $offsetY + 15, (Color-Hex "#0d1118"))
        }
        for ($y = 0; $y -lt 16; $y++) {
            $bmp.SetPixel(0, $offsetY + $y, (Color-Hex "#0d1118"))
            $bmp.SetPixel(15, $offsetY + $y, (Color-Hex "#0d1118"))
        }
        for ($x = 4; $x -le 11; $x++) {
            for ($y = 4; $y -le 11; $y++) {
                $bmp.SetPixel($x, $offsetY + $y, (Color-Hex $glows[$frame]))
            }
        }
        Draw-Star $bmp 8 ($offsetY + 8) (Color-Hex "#fff8d4") (Color-Hex "#ffe093")
    }
    Save-Png $bmp $path
}

function Create-Frame([string]$path) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex "#10242a") (Color-Hex "#30545d") 10
    foreach ($i in @(2, 5, 8, 11, 14)) {
        Draw-Line $bmp $i 0 0 $i (Color-Hex "#7ed0c1")
        Draw-Line $bmp 15 $i $i 15 (Color-Hex "#d8e6b7")
    }
    Draw-Border $bmp (Color-Hex "#0a1116") (Color-Hex "#9ce7d3")
    Save-Png $bmp $path
}

function Create-Altar([string]$path, [bool]$awakened) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex "#1d232c") (Color-Hex "#7d8c98") 10
    Draw-Border $bmp (Color-Hex "#11161d") (Color-Hex "#cad2da")
    $ringColor = if ($awakened) { Color-Hex "#f5f1bc" } else { Color-Hex "#8cb7cb" }
    $coreColor = if ($awakened) { Color-Hex "#fffbdc" } else { Color-Hex "#d7ebff" }
    $accentColor = if ($awakened) { Color-Hex "#e7d57d" } else { Color-Hex "#88d7ff" }
    Draw-Ring $bmp 8 8 4 $ringColor
    Draw-Star $bmp 8 8 $coreColor $accentColor
    Save-Png $bmp $path
}

function Create-Barrier([string]$path) {
    $bmp = New-Bitmap
    Fill-Noise $bmp (Color-Hex "#1c2026") (Color-Hex "#5d6a77") 10
    for ($i = 1; $i -lt 15; $i += 3) {
        Draw-Line $bmp $i 0 15 $i (Color-Hex "#a5c0d0")
        Draw-Line $bmp 0 $i $i 15 (Color-Hex "#6e8795")
    }
    Draw-Border $bmp (Color-Hex "#0d1116") (Color-Hex "#dce9f1")
    Save-Png $bmp $path
}

function Create-Cache([string]$path) {
    $frameHeight = 16
    $bmp = New-Bitmap 16 ($frameHeight * 4)
    $cores = @("#fff6b4", "#ffdb73", "#d6f2ff", "#fff0b7")
    for ($frame = 0; $frame -lt 4; $frame++) {
        $offsetY = $frame * $frameHeight
        for ($x = 0; $x -lt 16; $x++) {
            for ($y = 0; $y -lt 16; $y++) {
                $mix = (($x + $y) / 30.0)
                $base = Mix-Color (Color-Hex "#1a2130") (Color-Hex "#68778b") $mix
                $bmp.SetPixel($x, $offsetY + $y, $base)
            }
        }
        for ($x = 0; $x -lt 16; $x++) {
            $bmp.SetPixel($x, $offsetY, (Color-Hex "#12161f"))
            $bmp.SetPixel($x, $offsetY + 15, (Color-Hex "#12161f"))
        }
        for ($y = 0; $y -lt 16; $y++) {
            $bmp.SetPixel(0, $offsetY + $y, (Color-Hex "#12161f"))
            $bmp.SetPixel(15, $offsetY + $y, (Color-Hex "#12161f"))
        }
        for ($x = 4; $x -le 11; $x++) {
            for ($y = 5; $y -le 10; $y++) {
                $bmp.SetPixel($x, $offsetY + $y, (Color-Hex "#5e3f1f"))
            }
        }
        Draw-Star $bmp 8 ($offsetY + 8) (Color-Hex $cores[$frame]) (Color-Hex "#9fdfff")
    }
    Save-Png $bmp $path
}

function Create-ItemCanvas() {
    return New-Bitmap
}

function Create-CrystalItem([string]$path, [string]$fill, [string]$edge) {
    $bmp = Create-ItemCanvas
    Draw-Diamond $bmp 8 8 5 (Color-Hex $fill) (Color-Hex $edge)
    Draw-Line $bmp 6 5 10 9 (Shade-Color (Color-Hex $edge) 25)
    Draw-Line $bmp 8 3 8 12 (Shade-Color (Color-Hex $fill) 30)
    Save-Png $bmp $path
}

function Create-BerryItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-Diamond $bmp 6 9 3 (Color-Hex "#7f2f8a") (Color-Hex "#d88ef1")
    Draw-Diamond $bmp 10 10 3 (Color-Hex "#5a2370") (Color-Hex "#c57de4")
    Draw-Line $bmp 7 4 8 6 (Color-Hex "#8dc76b")
    Draw-Line $bmp 8 4 10 5 (Color-Hex "#c5f6a0")
    Save-Png $bmp $path
}

function Create-TartItem([string]$path) {
    $bmp = Create-ItemCanvas
    for ($x = 3; $x -le 12; $x++) {
        for ($y = 6; $y -le 12; $y++) {
            $bmp.SetPixel($x, $y, (Color-Hex "#d9b47c"))
        }
    }
    for ($x = 4; $x -le 11; $x++) {
        for ($y = 4; $y -le 8; $y++) {
            $bmp.SetPixel($x, $y, (Color-Hex "#7a3c86"))
        }
    }
    Draw-Line $bmp 4 5 11 5 (Color-Hex "#f3d9ff")
    Draw-Line $bmp 5 3 6 1 (Color-Hex "#9cd7b2")
    Draw-Line $bmp 8 3 9 1 (Color-Hex "#c4f3a9")
    Save-Png $bmp $path
}

function Create-SigilItem([string]$path, [string]$ring, [string]$core) {
    $bmp = Create-ItemCanvas
    Draw-Ring $bmp 8 8 5 (Color-Hex $ring)
    Draw-Star $bmp 8 8 (Color-Hex $core) (Shade-Color (Color-Hex $ring) 20)
    Draw-Line $bmp 8 1 8 3 (Color-Hex $ring)
    Draw-Line $bmp 1 8 3 8 (Color-Hex $ring)
    Draw-Line $bmp 13 8 15 8 (Color-Hex $ring)
    Draw-Line $bmp 8 13 8 15 (Color-Hex $ring)
    Save-Png $bmp $path
}

function Create-FiberItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-Line $bmp 4 3 10 13 (Color-Hex "#e8f1ff")
    Draw-Line $bmp 7 2 13 12 (Color-Hex "#8ed8ff")
    Draw-Line $bmp 3 8 12 6 (Color-Hex "#f9eeb1")
    Save-Png $bmp $path
}

function Create-CharmItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-Line $bmp 8 1 8 4 (Color-Hex "#d7edf6")
    Draw-Diamond $bmp 8 8 4 (Color-Hex "#5c7f95") (Color-Hex "#d4f1ff")
    Draw-Star $bmp 8 8 (Color-Hex "#fff7c8") (Color-Hex "#9ce3ff")
    Save-Png $bmp $path
}

function Create-SovereignWardItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-Ring $bmp 8 8 5 (Color-Hex "#8fd8ff")
    Draw-Diamond $bmp 8 8 3 (Color-Hex "#fff0b5") (Color-Hex "#d4f3ff")
    Draw-Line $bmp 8 1 8 4 (Color-Hex "#fff8d7")
    Draw-Line $bmp 3 8 13 8 (Color-Hex "#8fd8ff")
    Save-Png $bmp $path
}

function Create-ToolItem([string]$path, [string]$type) {
    $bmp = Create-ItemCanvas
    switch ($type) {
        "blade" {
            Draw-Line $bmp 4 3 11 10 (Color-Hex "#d6e7f2")
            Draw-Line $bmp 3 4 10 11 (Color-Hex "#89c8e1")
            Draw-ToolHandle $bmp 10 10 13 13
        }
        "pickaxe" {
            Draw-Line $bmp 8 4 8 13 (Color-Hex "#dfe9ef")
            Draw-Line $bmp 4 5 12 5 (Color-Hex "#9cd2e0")
            Draw-Line $bmp 4 6 6 8 (Color-Hex "#dfe9ef")
            Draw-Line $bmp 10 6 12 8 (Color-Hex "#dfe9ef")
            Draw-ToolHandle $bmp 8 8 8 14
        }
        "axe" {
            Draw-Line $bmp 8 3 8 14 (Color-Hex "#dfe9ef")
            Draw-Line $bmp 5 4 10 8 (Color-Hex "#91d2e5")
            Draw-Line $bmp 4 5 8 9 (Color-Hex "#dfe9ef")
            Draw-ToolHandle $bmp 8 8 8 14
        }
        "shovel" {
            Draw-Line $bmp 8 3 8 14 (Color-Hex "#dfe9ef")
            Draw-Diamond $bmp 8 5 3 (Color-Hex "#8bc9e1") (Color-Hex "#e8f3f7")
            Draw-ToolHandle $bmp 8 8 8 14
        }
        "glaive" {
            Draw-Line $bmp 6 2 11 7 (Color-Hex "#fff0b5")
            Draw-Line $bmp 5 3 10 8 (Color-Hex "#8fd8ff")
            Draw-Line $bmp 9 6 13 10 (Color-Hex "#d8f6ff")
            Draw-ToolHandle $bmp 8 8 4 14
        }
    }
    Save-Png $bmp $path
}

function Create-SpawnEggItem([string]$path, [string]$shell, [string]$spots) {
    $bmp = Create-ItemCanvas
    $shellColor = Color-Hex $shell
    $spotColor = Color-Hex $spots

    for ($x = 3; $x -le 12; $x++) {
        for ($y = 2; $y -le 13; $y++) {
            $nx = ($x - 7.5) / 4.8
            $ny = ($y - 7.5) / 6.0
            if ((($nx * $nx) + ($ny * $ny)) -le 1.0) {
                $shade = [int](($ny * -18) + ($nx * 6))
                $bmp.SetPixel($x, $y, (Shade-Color $shellColor $shade))
            }
        }
    }

    foreach ($dot in @(@(6, 5), @(9, 7), @(7, 10), @(10, 11), @(5, 8))) {
        Draw-Diamond $bmp $dot[0] $dot[1] 1 $spotColor (Shade-Color $spotColor 25)
    }

    Draw-Line $bmp 5 3 8 2 (Color-Hex "#fff7dd")
    Draw-Line $bmp 4 4 6 3 (Color-Hex "#fff7dd")
    Save-Png $bmp $path
}

function Load-ZipBitmap([string]$zipPath, [string]$entryName) {
    $zip = [System.IO.Compression.ZipFile]::OpenRead($zipPath)
    try {
        $entry = $zip.GetEntry($entryName)
        if ($null -eq $entry) {
            throw "Missing entry $entryName"
        }
        $stream = $entry.Open()
        try {
            $memory = New-Object System.IO.MemoryStream
            $stream.CopyTo($memory)
            $memory.Position = 0
            $source = [System.Drawing.Bitmap]::FromStream($memory)
            $copy = [System.Drawing.Bitmap]::new($source)
            $source.Dispose()
            $memory.Dispose()
            return $copy
        } finally {
            $stream.Dispose()
        }
    } finally {
        $zip.Dispose()
    }
}

function Recolor-Entity([System.Drawing.Bitmap]$bmp, [System.Drawing.Color]$dark, [System.Drawing.Color]$mid, [System.Drawing.Color]$light, [System.Drawing.Color]$accent) {
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        for ($y = 0; $y -lt $bmp.Height; $y++) {
            $px = $bmp.GetPixel($x, $y)
            if ($px.A -eq 0) {
                continue
            }
            $lum = ((0.2126 * $px.R) + (0.7152 * $px.G) + (0.0722 * $px.B)) / 255.0
            $mapped = if ($lum -lt 0.5) {
                Mix-Color $dark $mid ($lum * 2.0)
            } else {
                Mix-Color $mid $light (($lum - 0.5) * 2.0)
            }
            if ((($x + $y) % 11) -eq 0) {
                $mapped = Mix-Color $mapped $accent 0.35
            }
            $bmp.SetPixel($x, $y, ([System.Drawing.Color]::FromArgb($px.A, $mapped.R, $mapped.G, $mapped.B)))
        }
    }
}

Create-StoneBlock (Join-Path $blockDir "veilstone.png") "#20262f" "#495a66" "#7cc8d4"
Create-StoneBlock (Join-Path $blockDir "polished_veilstone.png") "#2a323c" "#718291" "#cfe8ef"
Create-StoneBlock (Join-Path $blockDir "mirror_grass.png") "#183034" "#4b7775" "#b1f1d3"
Create-StoneBlock (Join-Path $blockDir "moonmoss.png") "#172824" "#4f7566" "#b7f5c8"
Create-WoodPlanks (Join-Path $blockDir "gloamwood_planks.png") "#2f4959" "#6ca3bd"
Create-WoodLog (Join-Path $blockDir "gloamwood_log.png")
Create-Leaves (Join-Path $blockDir "shimmerleaf_leaves.png")
Create-Flower (Join-Path $blockDir "duskcap.png") "#5f6f8d" "#d9ecff" "#31465c"
Create-Flower (Join-Path $blockDir "moonblossom.png") "#cab8ff" "#fff2bb" "#92c99d"
Create-Flower (Join-Path $blockDir "rift_bloom.png") "#95dfff" "#fff9dc" "#6fb5c9"
Create-Flower (Join-Path $blockDir "veilstone_pile.png") "#8e95a1" "#dee3ef" "#566070"
Create-Glass (Join-Path $blockDir "riftglass.png")
Create-Lantern (Join-Path $blockDir "starlantern.png")
Create-Frame (Join-Path $blockDir "moonwoven_frame.png")
Create-Portal (Join-Path $blockDir "hush_portal.png")
Create-Altar (Join-Path $blockDir "sealed_altar.png") $false
Create-Altar (Join-Path $blockDir "awakened_altar.png") $true
Create-Barrier (Join-Path $blockDir "fogbound_barrier.png")
Create-Cache (Join-Path $blockDir "starlight_cache.png")

Create-CrystalItem (Join-Path $itemDir "veil_shard.png") "#7bc6d8" "#ecfbff"
Create-BerryItem (Join-Path $itemDir "hushberry.png")
Create-TartItem (Join-Path $itemDir "moonbroth_tart.png")
Create-FiberItem (Join-Path $itemDir "lunar_fiber.png")
Create-CrystalItem (Join-Path $itemDir "mirror_ingot.png") "#93b8c4" "#f3fcff"
Create-ToolItem (Join-Path $itemDir "mirror_blade.png") "blade"
Create-ToolItem (Join-Path $itemDir "mirror_pickaxe.png") "pickaxe"
Create-ToolItem (Join-Path $itemDir "mirror_axe.png") "axe"
Create-ToolItem (Join-Path $itemDir "mirror_shovel.png") "shovel"
Create-ToolItem (Join-Path $itemDir "sovereign_glaive.png") "glaive"
Create-CrystalItem (Join-Path $itemDir "starfragment.png") "#f2d56e" "#fff9d3"
Create-SigilItem (Join-Path $itemDir "warden_sigil.png") "#97b8cb" "#fff4c3"
Create-CharmItem (Join-Path $itemDir "hush_ward_charm.png")
Create-SovereignWardItem (Join-Path $itemDir "sovereign_ward.png")
Create-SigilItem (Join-Path $itemDir "astral_sigil.png") "#9bdcf7" "#fff0a8"
Create-CrystalItem (Join-Path $itemDir "gloamwood_sap.png") "#5ba8bb" "#e1fcff"
Create-CrystalItem (Join-Path $itemDir "sovereign_core.png") "#89dbff" "#fff7c9"
Create-SpawnEggItem (Join-Path $itemDir "mirror_stag_spawn_egg.png") "#d3ece6" "#6ca4aa"
Create-SpawnEggItem (Join-Path $itemDir "hushling_spawn_egg.png") "#2d444a" "#a9efe4"
Create-SpawnEggItem (Join-Path $itemDir "hush_sovereign_spawn_egg.png") "#0c1119" "#9ad9ff"
Create-SpawnEggItem (Join-Path $itemDir "lanternkeeper_spawn_egg.png") "#20303c" "#ffd78c"
Create-SpawnEggItem (Join-Path $itemDir "moonfallen_knight_spawn_egg.png") "#2b2f3b" "#dbe7ef"
Create-SpawnEggItem (Join-Path $itemDir "veilbound_watcher_spawn_egg.png") "#111725" "#91c8f1"
Create-SpawnEggItem (Join-Path $itemDir "warden_of_silence_spawn_egg.png") "#313941" "#d9edf3"

$mirrorStag = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/cow/temperate_cow.png"
Recolor-Entity $mirrorStag (Color-Hex "#1a2025") (Color-Hex "#5b7f86") (Color-Hex "#dff5ee") (Color-Hex "#e7d18e")
Save-Png $mirrorStag (Join-Path $entityDir "mirror_stag.png")

$hushling = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/zombie/zombie.png"
Recolor-Entity $hushling (Color-Hex "#13191d") (Color-Hex "#38646c") (Color-Hex "#9fe3da") (Color-Hex "#d5fff7")
Save-Png $hushling (Join-Path $entityDir "hushling.png")

$hushSovereign = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/skeleton/wither_skeleton.png"
Recolor-Entity $hushSovereign (Color-Hex "#05070c") (Color-Hex "#3d4c61") (Color-Hex "#b7d9f5") (Color-Hex "#fff1b0")
Save-Png $hushSovereign (Join-Path $entityDir "hush_sovereign.png")

$lanternkeeper = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/illager/pillager.png"
Recolor-Entity $lanternkeeper (Color-Hex "#12161c") (Color-Hex "#566a76") (Color-Hex "#d8e5ea") (Color-Hex "#ffd88d")
Save-Png $lanternkeeper (Join-Path $entityDir "lanternkeeper.png")

$moonfallenKnight = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/skeleton/wither_skeleton.png"
Recolor-Entity $moonfallenKnight (Color-Hex "#0d1117") (Color-Hex "#556271") (Color-Hex "#dce7ef") (Color-Hex "#9dd4ff")
Save-Png $moonfallenKnight (Join-Path $entityDir "moonfallen_knight.png")

$watcher = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/enderman/enderman.png"
Recolor-Entity $watcher (Color-Hex "#06070d") (Color-Hex "#24334b") (Color-Hex "#9dc5e8") (Color-Hex "#d0f6ff")
Save-Png $watcher (Join-Path $entityDir "veilbound_watcher.png")

$warden = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/skeleton/wither_skeleton.png"
Recolor-Entity $warden (Color-Hex "#0a0e12") (Color-Hex "#4c5965") (Color-Hex "#d8e7ef") (Color-Hex "#b4edf0")
Save-Png $warden (Join-Path $entityDir "warden_of_silence.png")

$icon = [System.Drawing.Bitmap]::new((Join-Path $itemDir "astral_sigil.png"))
$iconLarge = [System.Drawing.Bitmap]::new(128, 128)
$graphics = [System.Drawing.Graphics]::FromImage($iconLarge)
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$graphics.Clear((Color-Hex "#102029"))
$graphics.DrawImage($icon, 0, 0, 128, 128)
$graphics.Dispose()
$icon.Dispose()
Save-Png $iconLarge $iconPath

Normalize-ExistingTextures $blockDir
Normalize-ExistingTextures $itemDir
Normalize-ExistingTextures $entityDir
