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
$tileSize = 256
$entitySheetSize = 256

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

function Add-EdgeHighlights([System.Drawing.Bitmap]$bmp, [int]$boost = 16) {
    for ($x = 1; $x -lt ($bmp.Width - 1); $x++) {
        for ($y = 1; $y -lt ($bmp.Height - 1); $y++) {
            $px = $bmp.GetPixel($x, $y)
            if ($px.A -eq 0) {
                continue
            }

            $touchesTransparent = $false
            foreach ($offset in @(@(-1, 0), @(1, 0), @(0, -1), @(0, 1))) {
                $neighbor = $bmp.GetPixel($x + $offset[0], $y + $offset[1])
                if ($neighbor.A -eq 0) {
                    $touchesTransparent = $true
                    break
                }
            }

            if ($touchesTransparent) {
                $highlight = Shade-Color $px $boost
                $bmp.SetPixel($x, $y, ([System.Drawing.Color]::FromArgb($px.A, $highlight.R, $highlight.G, $highlight.B)))
            }
        }
    }
}

function Set-PixelIfOpaque([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [System.Drawing.Color]$color) {
    if ($x -lt 0 -or $x -ge $bmp.Width -or $y -lt 0 -or $y -ge $bmp.Height) {
        return
    }
    if ($bmp.GetPixel($x, $y).A -eq 0) {
        return
    }
    $bmp.SetPixel($x, $y, $color)
}

function Add-FiligreeOverlay([System.Drawing.Bitmap]$bmp, [int]$seedOffset = 0, [bool]$itemMode = $false) {
    $localRandom = [System.Random]::new(4242 + $seedOffset)
    $lineCount = if ($itemMode) { 10 } else { 7 }
    $glow = if ($itemMode) {
        [System.Drawing.Color]::FromArgb(72, 244, 248, 255)
    } else {
        [System.Drawing.Color]::FromArgb(56, 208, 228, 235)
    }

    for ($i = 0; $i -lt $lineCount; $i++) {
        $x1 = $localRandom.Next(0, $bmp.Width)
        $y1 = $localRandom.Next(0, $bmp.Height)
        $x2 = [Math]::Min($bmp.Width - 1, [Math]::Max(0, $x1 + $localRandom.Next(-18, 19)))
        $y2 = [Math]::Min($bmp.Height - 1, [Math]::Max(0, $y1 + $localRandom.Next(-18, 19)))
        $dx = [Math]::Abs($x2 - $x1)
        $dy = -[Math]::Abs($y2 - $y1)
        $sx = if ($x1 -lt $x2) { 1 } else { -1 }
        $sy = if ($y1 -lt $y2) { 1 } else { -1 }
        $err = $dx + $dy
        $cx = $x1
        $cy = $y1
        while ($true) {
            Set-PixelIfOpaque $bmp $cx $cy $glow
            if ($cx -eq $x2 -and $cy -eq $y2) {
                break
            }
            $e2 = 2 * $err
            if ($e2 -ge $dy) {
                $err += $dy
                $cx += $sx
            }
            if ($e2 -le $dx) {
                $err += $dx
                $cy += $sy
            }
        }
    }

    $sparkleCount = if ($itemMode) { 18 } else { 10 }
    for ($i = 0; $i -lt $sparkleCount; $i++) {
        $x = $localRandom.Next(2, $bmp.Width - 2)
        $y = $localRandom.Next(2, $bmp.Height - 2)
        $spark = [System.Drawing.Color]::FromArgb(110, 255, 250, 214)
        Set-PixelIfOpaque $bmp $x $y $spark
        Set-PixelIfOpaque $bmp ($x - 1) $y ([System.Drawing.Color]::FromArgb(60, 214, 238, 255))
        Set-PixelIfOpaque $bmp ($x + 1) $y ([System.Drawing.Color]::FromArgb(60, 214, 238, 255))
        Set-PixelIfOpaque $bmp $x ($y - 1) ([System.Drawing.Color]::FromArgb(60, 214, 238, 255))
        Set-PixelIfOpaque $bmp $x ($y + 1) ([System.Drawing.Color]::FromArgb(60, 214, 238, 255))
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
        Add-FiligreeOverlay $normalized ([Math]::Abs($path.GetHashCode()) % 1200) $false
        Add-EdgeHighlights $normalized 12
        return $normalized
    }
    if ($path -like "*\textures\block\*" -or $path -like "*\textures\item\*") {
        if ($bmp.Width -ne $tileSize -or $bmp.Height -ne $tileSize) {
            $normalized = Resize-Nearest $bmp $tileSize $tileSize
            $bmp.Dispose()
        }
        Add-HiResSurfaceDetails $normalized ([Math]::Abs($path.GetHashCode()) % 1000)
        $isItem = $path -like "*\textures\item\*"
        Add-FiligreeOverlay $normalized ([Math]::Abs($path.GetHashCode()) % 1500) $isItem
        $edgeBoost = if ($isItem) { 20 } else { 14 }
        Add-EdgeHighlights $normalized $edgeBoost
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
    $tempPath = "$path.tmp"
    if (Test-Path $tempPath) {
        Remove-Item $tempPath -Force
    }
    $finalBmp.Save($tempPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $finalBmp.Dispose()
    Move-Item -Force $tempPath $path
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

function Grid([double]$value) {
    return [int][Math]::Round(($tileSize / 16.0) * $value)
}

function Fill-Rect([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [int]$width, [int]$height, [System.Drawing.Color]$color) {
    for ($px = $x; $px -lt ($x + $width); $px++) {
        for ($py = $y; $py -lt ($y + $height); $py++) {
            if ($px -ge 0 -and $px -lt $bmp.Width -and $py -ge 0 -and $py -lt $bmp.Height) {
                $bmp.SetPixel($px, $py, $color)
            }
        }
    }
}

function Draw-BrushPoint([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [int]$radius, [System.Drawing.Color]$color) {
    for ($dx = -$radius; $dx -le $radius; $dx++) {
        for ($dy = -$radius; $dy -le $radius; $dy++) {
            if (($dx * $dx) + ($dy * $dy) -le ($radius * $radius)) {
                $px = $x + $dx
                $py = $y + $dy
                if ($px -ge 0 -and $px -lt $bmp.Width -and $py -ge 0 -and $py -lt $bmp.Height) {
                    $bmp.SetPixel($px, $py, $color)
                }
            }
        }
    }
}

function Draw-WideLine([System.Drawing.Bitmap]$bmp, [int]$x1, [int]$y1, [int]$x2, [int]$y2, [System.Drawing.Color]$color, [int]$thickness = 1) {
    $dx = [Math]::Abs($x2 - $x1)
    $dy = -[Math]::Abs($y2 - $y1)
    $sx = if ($x1 -lt $x2) { 1 } else { -1 }
    $sy = if ($y1 -lt $y2) { 1 } else { -1 }
    $err = $dx + $dy

    while ($true) {
        Draw-BrushPoint $bmp $x1 $y1 $thickness $color
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

function Draw-RingWide([System.Drawing.Bitmap]$bmp, [int]$cx, [int]$cy, [int]$radius, [System.Drawing.Color]$color, [int]$thickness = 1) {
    for ($x = 0; $x -lt $bmp.Width; $x++) {
        for ($y = 0; $y -lt $bmp.Height; $y++) {
            $distance = [Math]::Sqrt((($x - $cx) * ($x - $cx)) + (($y - $cy) * ($y - $cy)))
            if ($distance -ge ($radius - $thickness) -and $distance -le ($radius + $thickness)) {
                $bmp.SetPixel($x, $y, $color)
            }
        }
    }
}

function Draw-LineGrid([System.Drawing.Bitmap]$bmp, [double]$x1, [double]$y1, [double]$x2, [double]$y2, [System.Drawing.Color]$color, [double]$thickness = 0.7) {
    Draw-WideLine $bmp (Grid $x1) (Grid $y1) (Grid $x2) (Grid $y2) $color ([Math]::Max(1, (Grid $thickness)))
}

function Draw-DiamondGrid([System.Drawing.Bitmap]$bmp, [double]$cx, [double]$cy, [double]$radius, [System.Drawing.Color]$fill, [System.Drawing.Color]$edge) {
    Draw-Diamond $bmp (Grid $cx) (Grid $cy) (Grid $radius) $fill $edge
}

function Draw-RingGrid([System.Drawing.Bitmap]$bmp, [double]$cx, [double]$cy, [double]$radius, [System.Drawing.Color]$color, [double]$thickness = 0.8) {
    Draw-RingWide $bmp (Grid $cx) (Grid $cy) (Grid $radius) $color ([Math]::Max(1, (Grid $thickness)))
}

function Draw-StarGrid([System.Drawing.Bitmap]$bmp, [double]$cx, [double]$cy, [System.Drawing.Color]$core, [System.Drawing.Color]$accent, [double]$size = 1.0) {
    $centerX = Grid $cx
    $centerY = Grid $cy
    $arm = [Math]::Max(2, (Grid ($size * 0.7)))
    $thickness = [Math]::Max(1, (Grid ($size * 0.35)))
    Draw-BrushPoint $bmp $centerX $centerY $thickness $core
    foreach ($offset in @(@($arm, 0), @(-$arm, 0), @(0, $arm), @(0, -$arm), @($arm, $arm), @(-$arm, $arm), @($arm, -$arm), @(-$arm, -$arm))) {
        Draw-BrushPoint $bmp ($centerX + $offset[0]) ($centerY + $offset[1]) $thickness $accent
    }
}

function Draw-ToolHandleGrid([System.Drawing.Bitmap]$bmp, [double]$x1, [double]$y1, [double]$x2, [double]$y2) {
    Draw-LineGrid $bmp $x1 $y1 $x2 $y2 (Color-Hex "#4a2e1f") 0.7
    Draw-LineGrid $bmp ($x1 + 0.7) $y1 ($x2 + 0.7) $y2 (Color-Hex "#805139") 0.45
}

function Fill-RectGrid([System.Drawing.Bitmap]$bmp, [double]$x, [double]$y, [double]$width, [double]$height, [System.Drawing.Color]$color) {
    Fill-Rect $bmp (Grid $x) (Grid $y) ([Math]::Max(1, (Grid $width))) ([Math]::Max(1, (Grid $height))) $color
}

function Create-StoneBlock([string]$path, [string]$baseDark, [string]$baseLight, [string]$vein) {
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex $baseDark) (Color-Hex $baseLight) 14
    for ($i = 0; $i -lt 18; $i++) {
        Draw-WideLine $bmp $random.Next(0, [Math]::Floor($tileSize * 0.35)) $random.Next(0, $tileSize) $random.Next([Math]::Floor($tileSize * 0.55), $tileSize) $random.Next(0, $tileSize) (Color-Hex $vein) ([Math]::Max(1, [int]($tileSize / 42)))
    }
    for ($i = 0; $i -lt 26; $i++) {
        $cx = $random.Next(6, $tileSize - 6)
        $cy = $random.Next(6, $tileSize - 6)
        Draw-RingWide $bmp $cx $cy ([Math]::Max(2, [int]($tileSize / 18))) ([System.Drawing.Color]::FromArgb(42, (Color-Hex $vein).R, (Color-Hex $vein).G, (Color-Hex $vein).B)) 1
    }
    Save-Png $bmp $path
}

function Create-WoodPlanks([string]$path, [string]$base, [string]$accent) {
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex "#18232d") (Color-Hex $base) 10
    $plankHeight = [Math]::Max(12, [int]($tileSize / 4))
    for ($y = $plankHeight; $y -lt $tileSize; $y += $plankHeight) {
        Draw-WideLine $bmp 0 $y ($tileSize - 1) $y (Color-Hex "#0c1319") ([Math]::Max(1, [int]($tileSize / 64)))
        Draw-WideLine $bmp 0 ($y + [Math]::Max(1, [int]($tileSize / 96))) ($tileSize - 1) ($y + [Math]::Max(1, [int]($tileSize / 96))) (Color-Hex $accent) ([Math]::Max(1, [int]($tileSize / 96)))
    }
    for ($x = 0; $x -lt $tileSize; $x += [Math]::Max(12, [int]($tileSize / 5))) {
        Draw-WideLine $bmp $x 0 ([Math]::Min($tileSize - 1, $x + [Math]::Max(4, [int]($tileSize / 9)))) ($tileSize - 1) (Shade-Color (Color-Hex $accent) -30) 1
    }
    for ($i = 0; $i -lt 48; $i++) {
        $kx = $random.Next(8, $tileSize - 8)
        $ky = $random.Next(8, $tileSize - 8)
        Draw-RingWide $bmp $kx $ky ([Math]::Max(2, [int]($tileSize / 30))) ([System.Drawing.Color]::FromArgb(50, 255, 255, 255)) 1
    }
    Save-Png $bmp $path
}

function Create-WoodLog([string]$path) {
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex "#11161d") (Color-Hex "#355266") 14
    for ($x = [Math]::Max(4, [int]($tileSize / 18)); $x -lt $tileSize; $x += [Math]::Max(10, [int]($tileSize / 6))) {
        Draw-WideLine $bmp $x 0 $x ($tileSize - 1) (Color-Hex "#5a879f") ([Math]::Max(1, [int]($tileSize / 64)))
    }
    Draw-WideLine $bmp ([int]($tileSize * 0.46)) 0 ([int]($tileSize * 0.46)) ($tileSize - 1) (Color-Hex "#91d0df") ([Math]::Max(1, [int]($tileSize / 80)))
    Draw-WideLine $bmp ([int]($tileSize * 0.52)) 0 ([int]($tileSize * 0.52)) ($tileSize - 1) (Color-Hex "#91d0df") ([Math]::Max(1, [int]($tileSize / 80)))
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
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex "#10242a") (Color-Hex "#30545d") 10
    foreach ($i in @(2, 5, 8, 11, 14)) {
        $scaled = Grid $i
        Draw-WideLine $bmp $scaled 0 0 $scaled (Color-Hex "#7ed0c1") ([Math]::Max(1, [int]($tileSize / 72)))
        Draw-WideLine $bmp ($tileSize - 1) $scaled $scaled ($tileSize - 1) (Color-Hex "#d8e6b7") ([Math]::Max(1, [int]($tileSize / 72)))
    }
    Draw-RingWide $bmp (Grid 8) (Grid 8) (Grid 4.5) ([System.Drawing.Color]::FromArgb(65, 217, 245, 199)) ([Math]::Max(1, [int]($tileSize / 96)))
    Draw-Border $bmp (Color-Hex "#0a1116") (Color-Hex "#9ce7d3")
    Save-Png $bmp $path
}

function Create-FrameTop([string]$path) {
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex "#162732") (Color-Hex "#3f6670") 10
    Draw-RingGrid $bmp 8 8 5.2 (Color-Hex "#d9f5c7") 0.75
    Draw-RingGrid $bmp 8 8 3.3 (Color-Hex "#86d7d8") 0.45
    Draw-StarGrid $bmp 8 8 (Color-Hex "#fff4bf") (Color-Hex "#8fe1ff") 1.1
    Draw-Border $bmp (Color-Hex "#0a1116") (Color-Hex "#9ce7d3")
    Save-Png $bmp $path
}

function Create-FrameSide([string]$path) {
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex "#10242a") (Color-Hex "#30545d") 10
    for ($y = 2; $y -lt 15; $y += 4) {
        Draw-LineGrid $bmp 2 $y 13 $y (Color-Hex "#9ce7d3") 0.4
        Draw-LineGrid $bmp 4 ($y + 1) 11 ($y + 1) (Color-Hex "#dff3bc") 0.3
    }
    Draw-Border $bmp (Color-Hex "#0a1116") (Color-Hex "#9ce7d3")
    Save-Png $bmp $path
}

function Create-FrameBottom([string]$path) {
    $bmp = New-Bitmap $tileSize $tileSize
    Fill-Noise $bmp (Color-Hex "#0d1b20") (Color-Hex "#274952") 8
    for ($x = 3; $x -lt 14; $x += 4) {
        Draw-LineGrid $bmp $x 3 $x 12 (Color-Hex "#5ea2b0") 0.35
    }
    Draw-Border $bmp (Color-Hex "#0a1116") (Color-Hex "#7fc0c3")
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
    return New-Bitmap $tileSize $tileSize
}

function Create-CrystalItem([string]$path, [string]$fill, [string]$edge) {
    $bmp = Create-ItemCanvas
    Draw-DiamondGrid $bmp 8 8 5.3 (Color-Hex $fill) (Color-Hex $edge)
    Draw-LineGrid $bmp 5.4 4.4 10.8 9.6 (Shade-Color (Color-Hex $edge) 25) 0.55
    Draw-LineGrid $bmp 8 2.2 8 13.4 (Shade-Color (Color-Hex $fill) 30) 0.45
    Draw-LineGrid $bmp 6.2 6.5 9.6 6.5 (Color-Hex "#f7fdff") 0.3
    Save-Png $bmp $path
}

function Create-BerryItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-DiamondGrid $bmp 6 9 3.2 (Color-Hex "#7f2f8a") (Color-Hex "#d88ef1")
    Draw-DiamondGrid $bmp 10 10 3.1 (Color-Hex "#5a2370") (Color-Hex "#c57de4")
    Draw-LineGrid $bmp 7 3.6 8 6.1 (Color-Hex "#8dc76b") 0.45
    Draw-LineGrid $bmp 8 4.0 10.7 5.1 (Color-Hex "#c5f6a0") 0.35
    Draw-StarGrid $bmp 5.2 8.6 (Color-Hex "#f5c0ff") (Color-Hex "#ffffff") 0.4
    Save-Png $bmp $path
}

function Create-TartItem([string]$path) {
    $bmp = Create-ItemCanvas
    Fill-RectGrid $bmp 2.8 6.0 9.4 6.8 (Color-Hex "#d9b47c")
    Fill-RectGrid $bmp 3.8 4.0 7.6 4.8 (Color-Hex "#7a3c86")
    Draw-LineGrid $bmp 3.8 5.1 11.2 5.1 (Color-Hex "#f3d9ff") 0.35
    Draw-LineGrid $bmp 5.2 3.2 6.4 1.0 (Color-Hex "#9cd7b2") 0.4
    Draw-LineGrid $bmp 8.4 3.2 9.6 1.0 (Color-Hex "#c4f3a9") 0.4
    Draw-LineGrid $bmp 4.0 11.8 11.5 11.8 (Color-Hex "#8d6036") 0.45
    Save-Png $bmp $path
}

function Create-SigilItem([string]$path, [string]$ring, [string]$core) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 8 8 5.2 (Color-Hex $ring) 0.75
    Draw-RingGrid $bmp 8 8 3.4 (Shade-Color (Color-Hex $ring) 15) 0.35
    Draw-StarGrid $bmp 8 8 (Color-Hex $core) (Shade-Color (Color-Hex $ring) 20) 1.25
    Draw-LineGrid $bmp 8 0.7 8 3.0 (Color-Hex $ring) 0.35
    Draw-LineGrid $bmp 0.7 8 3.0 8 (Color-Hex $ring) 0.35
    Draw-LineGrid $bmp 13.0 8 15.3 8 (Color-Hex $ring) 0.35
    Draw-LineGrid $bmp 8 13.0 8 15.3 (Color-Hex $ring) 0.35
    Save-Png $bmp $path
}

function Create-FiberItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-LineGrid $bmp 4 3 10 13 (Color-Hex "#e8f1ff") 0.5
    Draw-LineGrid $bmp 7 2 13 12 (Color-Hex "#8ed8ff") 0.45
    Draw-LineGrid $bmp 3 8 12 6 (Color-Hex "#f9eeb1") 0.35
    Draw-LineGrid $bmp 5 5 11 11 (Color-Hex "#d4f9ff") 0.25
    Save-Png $bmp $path
}

function Create-CharmItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-LineGrid $bmp 8 1 8 4 (Color-Hex "#d7edf6") 0.4
    Draw-DiamondGrid $bmp 8 8 4.2 (Color-Hex "#5c7f95") (Color-Hex "#d4f1ff")
    Draw-StarGrid $bmp 8 8 (Color-Hex "#fff7c8") (Color-Hex "#9ce3ff") 1.0
    Draw-RingGrid $bmp 8 8 2.4 (Color-Hex "#a8d7ef") 0.25
    Save-Png $bmp $path
}

function Create-SovereignWardItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 8 8 5.3 (Color-Hex "#8fd8ff") 0.75
    Draw-RingGrid $bmp 8 8 3.9 (Color-Hex "#d8f5ff") 0.25
    Draw-DiamondGrid $bmp 8 8 3.4 (Color-Hex "#fff0b5") (Color-Hex "#d4f3ff")
    Draw-LineGrid $bmp 8 1 8 4 (Color-Hex "#fff8d7") 0.35
    Draw-LineGrid $bmp 3 8 13 8 (Color-Hex "#8fd8ff") 0.35
    Save-Png $bmp $path
}

function Create-KeyItem([string]$path, [string]$metal, [string]$gem) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 5 10 2.3 (Color-Hex $metal) 0.45
    Draw-LineGrid $bmp 5 3 5 11 (Color-Hex $metal) 0.45
    Draw-LineGrid $bmp 5 5 10 5 (Shade-Color (Color-Hex $metal) 20) 0.4
    Draw-LineGrid $bmp 9 5 9 7 (Shade-Color (Color-Hex $metal) -12) 0.35
    Draw-LineGrid $bmp 7 5 7 8 (Shade-Color (Color-Hex $metal) -6) 0.3
    Draw-DiamondGrid $bmp 5 2 1.1 (Color-Hex $gem) (Shade-Color (Color-Hex $gem) 24)
    Save-Png $bmp $path
}

function Create-SanctumKeyItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 4.8 10.3 2.5 (Color-Hex "#9fc6d8") 0.55
    Draw-LineGrid $bmp 4.8 2.3 4.8 11.2 (Color-Hex "#dcebf1") 0.55
    Draw-LineGrid $bmp 4.8 5.2 10.8 5.2 (Color-Hex "#7fdfff") 0.45
    Draw-LineGrid $bmp 9.0 5.2 9.0 7.8 (Color-Hex "#6aa8c4") 0.35
    Draw-LineGrid $bmp 7.0 5.2 7.0 8.8 (Color-Hex "#dcebf1") 0.3
    Draw-DiamondGrid $bmp 4.8 1.8 1.4 (Color-Hex "#9ef4ff") (Color-Hex "#f4ffff")
    Draw-RingGrid $bmp 4.8 10.3 1.1 (Color-Hex "#e8ffff") 0.2
    Save-Png $bmp $path
}

function Create-CathedralSealItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 8 8 5.3 (Color-Hex "#e6d3a1") 0.75
    Draw-RingGrid $bmp 8 8 3.7 (Color-Hex "#fff3c6") 0.3
    Draw-StarGrid $bmp 8 8 (Color-Hex "#fff4c4") (Color-Hex "#b1d9ff") 1.1
    Draw-LineGrid $bmp 8 1.4 8 4.1 (Color-Hex "#fff0b4") 0.3
    Draw-LineGrid $bmp 8 11.7 8 14.5 (Color-Hex "#fff0b4") 0.3
    Draw-LineGrid $bmp 1.8 8 4.4 8 (Color-Hex "#d8c494") 0.3
    Draw-LineGrid $bmp 11.6 8 14.2 8 (Color-Hex "#d8c494") 0.3
    Save-Png $bmp $path
}

function Create-WardenSigilItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 8 8 5.6 (Color-Hex "#84bdd7") 0.8
    Draw-RingGrid $bmp 8 8 4.2 (Color-Hex "#dff7ff") 0.25
    Draw-StarGrid $bmp 8 8 (Color-Hex "#fff4c6") (Color-Hex "#8edbff") 1.2
    Draw-LineGrid $bmp 4.0 4.0 12.0 12.0 (Color-Hex "#8edbff") 0.25
    Draw-LineGrid $bmp 12.0 4.0 4.0 12.0 (Color-Hex "#8edbff") 0.25
    Draw-RingGrid $bmp 8 8 2.0 (Color-Hex "#ffffff") 0.18
    Save-Png $bmp $path
}

function Create-AstralSigilItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-RingGrid $bmp 8 8 5.3 (Color-Hex "#9bdcf7") 0.75
    Draw-StarGrid $bmp 8 8 (Color-Hex "#fff0a8") (Color-Hex "#dff9ff") 1.25
    Draw-LineGrid $bmp 8 1.2 8 4.4 (Color-Hex "#fff7d0") 0.3
    Draw-LineGrid $bmp 8 11.6 8 14.8 (Color-Hex "#fff7d0") 0.3
    Draw-LineGrid $bmp 1.2 8 4.4 8 (Color-Hex "#c7eeff") 0.3
    Draw-LineGrid $bmp 11.6 8 14.8 8 (Color-Hex "#c7eeff") 0.3
    Draw-RingGrid $bmp 8 8 2.5 (Color-Hex "#ffffff") 0.18
    Save-Png $bmp $path
}

function Create-SovereignCoreItem([string]$path) {
    $bmp = Create-ItemCanvas
    Draw-DiamondGrid $bmp 8 8 4.0 (Color-Hex "#9de6ff") (Color-Hex "#f4ffff")
    Draw-DiamondGrid $bmp 8 8 2.4 (Color-Hex "#fff3b4") (Color-Hex "#d8f6ff")
    Draw-StarGrid $bmp 8 8 (Color-Hex "#fff7d2") (Color-Hex "#8fd8ff") 1.0
    Draw-LineGrid $bmp 8 1.5 8 4.0 (Color-Hex "#8fd8ff") 0.28
    Draw-LineGrid $bmp 8 12.0 8 14.5 (Color-Hex "#8fd8ff") 0.28
    Draw-LineGrid $bmp 1.5 8 4.0 8 (Color-Hex "#8fd8ff") 0.28
    Draw-LineGrid $bmp 12.0 8 14.5 8 (Color-Hex "#8fd8ff") 0.28
    Save-Png $bmp $path
}

function Create-ToolItem([string]$path, [string]$type) {
    $bmp = Create-ItemCanvas
    switch ($type) {
        "blade" {
            Draw-LineGrid $bmp 3.8 2.8 11.2 10.4 (Color-Hex "#d6e7f2") 0.55
            Draw-LineGrid $bmp 3.0 4.0 10.2 11.2 (Color-Hex "#89c8e1") 0.4
            Draw-ToolHandleGrid $bmp 10.0 10.2 13.3 13.6
            Draw-LineGrid $bmp 5.0 4.3 8.2 7.5 (Color-Hex "#ffffff") 0.2
        }
        "pickaxe" {
            Draw-LineGrid $bmp 8 4 8 13.5 (Color-Hex "#dfe9ef") 0.5
            Draw-LineGrid $bmp 4 5 12 5 (Color-Hex "#9cd2e0") 0.55
            Draw-LineGrid $bmp 4 6 6.2 8.3 (Color-Hex "#dfe9ef") 0.4
            Draw-LineGrid $bmp 10 6 12.2 8.3 (Color-Hex "#dfe9ef") 0.4
            Draw-ToolHandleGrid $bmp 8 8.3 8 14.2
        }
        "axe" {
            Draw-LineGrid $bmp 8 3 8 14.2 (Color-Hex "#dfe9ef") 0.5
            Draw-LineGrid $bmp 5 4 10.5 8.3 (Color-Hex "#91d2e5") 0.6
            Draw-LineGrid $bmp 4 5 8.5 9.2 (Color-Hex "#dfe9ef") 0.45
            Draw-ToolHandleGrid $bmp 8 8 8 14.2
        }
        "shovel" {
            Draw-LineGrid $bmp 8 3 8 14.2 (Color-Hex "#dfe9ef") 0.5
            Draw-DiamondGrid $bmp 8 5 3.2 (Color-Hex "#8bc9e1") (Color-Hex "#e8f3f7")
            Draw-ToolHandleGrid $bmp 8 8.4 8 14.2
        }
        "glaive" {
            Draw-LineGrid $bmp 6 2 11.4 7.2 (Color-Hex "#fff0b5") 0.6
            Draw-LineGrid $bmp 5 3 10.4 8.2 (Color-Hex "#8fd8ff") 0.45
            Draw-LineGrid $bmp 9 6 13.2 10.5 (Color-Hex "#d8f6ff") 0.35
            Draw-ToolHandleGrid $bmp 8.2 8.2 4.2 14.4
        }
    }
    Save-Png $bmp $path
}

function Create-ArmorItem([string]$path, [string]$type) {
    $bmp = Create-ItemCanvas
    switch ($type) {
        "helmet" {
            Draw-LineGrid $bmp 4 5 11.2 5 (Color-Hex "#dfeaf2") 0.5
            Draw-LineGrid $bmp 3 6 4.2 11.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 12 6 10.8 11.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 5 11.2 10.2 11.2 (Color-Hex "#dfeaf2") 0.45
            Draw-LineGrid $bmp 6 7 9.2 7 (Color-Hex "#fff4bf") 0.35
        }
        "chestplate" {
            Draw-LineGrid $bmp 5 3 10.2 3 (Color-Hex "#dfeaf2") 0.45
            Draw-LineGrid $bmp 4 4 4 12.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 11 4 11 12.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 6 4 6 7.2 (Color-Hex "#dfeaf2") 0.35
            Draw-LineGrid $bmp 9 4 9 7.2 (Color-Hex "#dfeaf2") 0.35
            Draw-LineGrid $bmp 5 12.2 10.2 12.2 (Color-Hex "#dfeaf2") 0.45
            Draw-StarGrid $bmp 8 8 (Color-Hex "#fff0b5") (Color-Hex "#9ce3ff") 0.9
        }
        "leggings" {
            Draw-LineGrid $bmp 5 3 10.1 3 (Color-Hex "#dfeaf2") 0.45
            Draw-LineGrid $bmp 5 4 5 12.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 10 4 10 12.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 7 7 7 13.2 (Color-Hex "#dfeaf2") 0.35
            Draw-LineGrid $bmp 8 7 8 13.2 (Color-Hex "#dfeaf2") 0.35
        }
        "boots" {
            Draw-LineGrid $bmp 4 8 6.3 8 (Color-Hex "#dfeaf2") 0.45
            Draw-LineGrid $bmp 9 8 11.3 8 (Color-Hex "#dfeaf2") 0.45
            Draw-LineGrid $bmp 4 9 4 12.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 11 9 11 12.2 (Color-Hex "#89c8e1") 0.45
            Draw-LineGrid $bmp 5 12.2 7.2 12.2 (Color-Hex "#dfeaf2") 0.4
            Draw-LineGrid $bmp 8 12.2 10.2 12.2 (Color-Hex "#dfeaf2") 0.4
        }
    }
    Save-Png $bmp $path
}

function Create-SovereignArmorItem([string]$path, [string]$type) {
    $bmp = Create-ItemCanvas
    $steel = Color-Hex "#dff5ff"
    $aura = Color-Hex "#8fd8ff"
    $gold = Color-Hex "#fff0b5"
    switch ($type) {
        "helmet" {
            Draw-LineGrid $bmp 4 5 11.2 5 $steel 0.5
            Draw-LineGrid $bmp 3 6 4.2 11.2 $aura 0.45
            Draw-LineGrid $bmp 12 6 10.8 11.2 $aura 0.45
            Draw-LineGrid $bmp 5 11.2 10.2 11.2 $steel 0.45
            Draw-StarGrid $bmp 8 4.7 $gold $aura 0.8
            Draw-LineGrid $bmp 8 1.8 8 3.8 $gold 0.3
        }
        "chestplate" {
            Draw-LineGrid $bmp 5 3 10.2 3 $steel 0.45
            Draw-LineGrid $bmp 4 4 4 12.2 $aura 0.45
            Draw-LineGrid $bmp 11 4 11 12.2 $aura 0.45
            Draw-LineGrid $bmp 5 12.2 10.2 12.2 $steel 0.45
            Draw-LineGrid $bmp 6 4 6 7.2 $steel 0.35
            Draw-LineGrid $bmp 9 4 9 7.2 $steel 0.35
            Draw-StarGrid $bmp 8 8 $gold $aura 1.1
            Draw-RingGrid $bmp 8 8 2.2 $aura 0.25
        }
        "leggings" {
            Draw-LineGrid $bmp 5 3 10.1 3 $steel 0.45
            Draw-LineGrid $bmp 5 4 5 12.2 $aura 0.45
            Draw-LineGrid $bmp 10 4 10 12.2 $aura 0.45
            Draw-LineGrid $bmp 7 7 7 13.2 $steel 0.35
            Draw-LineGrid $bmp 8 7 8 13.2 $steel 0.35
            Draw-LineGrid $bmp 6 5 9 5 $gold 0.25
        }
        "boots" {
            Draw-LineGrid $bmp 4 8 6.3 8 $steel 0.45
            Draw-LineGrid $bmp 9 8 11.3 8 $steel 0.45
            Draw-LineGrid $bmp 4 9 4 12.2 $aura 0.45
            Draw-LineGrid $bmp 11 9 11 12.2 $aura 0.45
            Draw-LineGrid $bmp 5 12.2 7.2 12.2 $steel 0.4
            Draw-LineGrid $bmp 8 12.2 10.2 12.2 $steel 0.4
            Draw-StarGrid $bmp 5.6 9.2 $gold $aura 0.5
            Draw-StarGrid $bmp 10.0 9.2 $gold $aura 0.5
        }
    }
    Save-Png $bmp $path
}

function Create-ArmorLayer([string]$path, [bool]$leggings) {
    $bmp = New-Bitmap 64 64
    Fill-Noise $bmp (Color-Hex "#1b2531") (Color-Hex "#8bbfd8") 12
    Draw-Border $bmp (Color-Hex "#0c1017") (Color-Hex "#d9eef8")
    for ($x = 6; $x -lt 58; $x += 8) {
        Draw-Line $bmp $x 0 $x 63 (Color-Hex "#4e7488")
    }
    for ($y = 6; $y -lt 58; $y += 8) {
        Draw-Line $bmp 0 $y 63 $y (Color-Hex "#273847")
    }
    if ($leggings) {
        Draw-Ring $bmp 32 34 10 (Color-Hex "#d7f3ff")
        Draw-Line $bmp 24 34 40 34 (Color-Hex "#fff2bf")
    } else {
        Draw-Ring $bmp 32 22 11 (Color-Hex "#d7f3ff")
        Draw-Star $bmp 32 22 (Color-Hex "#fff2bf") (Color-Hex "#8fd8ff")
    }
    Save-Png $bmp $path
}

function Create-SovereignArmorLayer([string]$path, [bool]$leggings) {
    $bmp = New-Bitmap 64 64
    Fill-Noise $bmp (Color-Hex "#0e1520") (Color-Hex "#79b9d8") 12
    Draw-Border $bmp (Color-Hex "#06090f") (Color-Hex "#dff7ff")
    for ($x = 8; $x -lt 56; $x += 8) {
        Draw-Line $bmp $x 0 $x 63 (Color-Hex "#335468")
    }
    for ($y = 8; $y -lt 56; $y += 8) {
        Draw-Line $bmp 0 $y 63 $y (Color-Hex "#142230")
    }
    if ($leggings) {
        Draw-Ring $bmp 32 34 10 (Color-Hex "#9fdfff")
        Draw-Line $bmp 24 34 40 34 (Color-Hex "#fff0b5")
    } else {
        Draw-Ring $bmp 32 22 11 (Color-Hex "#9fdfff")
        Draw-Star $bmp 32 22 (Color-Hex "#fff0b5") (Color-Hex "#dff7ff")
    }
    Save-Png $bmp $path
}

function Create-SpawnEggItem([string]$path, [string]$shell, [string]$spots) {
    $bmp = Create-ItemCanvas
    $shellColor = Color-Hex $shell
    $spotColor = Color-Hex $spots

    $minX = Grid 3
    $maxX = Grid 12
    $minY = Grid 2
    $maxY = Grid 13
    $centerX = Grid 7.5
    $centerY = Grid 7.5
    $radiusX = [double](Grid 4.8)
    $radiusY = [double](Grid 6.0)

    for ($x = $minX; $x -le $maxX; $x++) {
        for ($y = $minY; $y -le $maxY; $y++) {
            $nx = ($x - $centerX) / $radiusX
            $ny = ($y - $centerY) / $radiusY
            if ((($nx * $nx) + ($ny * $ny)) -le 1.0) {
                $shade = [int](($ny * -18) + ($nx * 6))
                $bmp.SetPixel($x, $y, (Shade-Color $shellColor $shade))
            }
        }
    }

    foreach ($dot in @(@(6, 5), @(9, 7), @(7, 10), @(10, 11), @(5, 8))) {
        Draw-DiamondGrid $bmp $dot[0] $dot[1] 1.0 $spotColor (Shade-Color $spotColor 25)
    }

    Draw-LineGrid $bmp 5 3 8 2 (Color-Hex "#fff7dd") 0.3
    Draw-LineGrid $bmp 4 4 6 3 (Color-Hex "#fff7dd") 0.25
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

function Paint-HushlingDetails([System.Drawing.Bitmap]$bmp) {
    $eye = [System.Drawing.Color]::FromArgb(255, 214, 255, 242)
    $glow = [System.Drawing.Color]::FromArgb(220, 130, 232, 221)
    $scar = [System.Drawing.Color]::FromArgb(160, 78, 136, 132)
    Draw-BrushPoint $bmp 10 19 1 $eye
    Draw-BrushPoint $bmp 14 19 1 $eye
    Draw-BrushPoint $bmp 10 19 2 $glow
    Draw-BrushPoint $bmp 14 19 2 $glow
    Draw-WideLine $bmp 8 22 16 26 $scar 1
    Draw-WideLine $bmp 9 31 14 37 $scar 1
    Draw-WideLine $bmp 18 35 23 49 ([System.Drawing.Color]::FromArgb(140, 180, 230, 225)) 1
}

function Paint-WatcherDetails([System.Drawing.Bitmap]$bmp) {
    $eye = [System.Drawing.Color]::FromArgb(255, 181, 227, 255)
    $veil = [System.Drawing.Color]::FromArgb(120, 112, 173, 211)
    $rune = [System.Drawing.Color]::FromArgb(185, 214, 244, 255)
    Draw-BrushPoint $bmp 9 18 1 $eye
    Draw-BrushPoint $bmp 14 18 1 $eye
    Draw-WideLine $bmp 6 33 18 46 $veil 1
    Draw-WideLine $bmp 18 33 6 46 $veil 1
    Draw-WideLine $bmp 12 20 12 30 $rune 1
    Draw-WideLine $bmp 9 25 15 25 $rune 1
}

function Paint-StagDetails([System.Drawing.Bitmap]$bmp) {
    $glow = [System.Drawing.Color]::FromArgb(210, 218, 250, 238)
    $accent = [System.Drawing.Color]::FromArgb(165, 129, 209, 214)
    Draw-WideLine $bmp 7 9 3 1 $glow 1
    Draw-WideLine $bmp 9 9 7 1 $glow 1
    Draw-WideLine $bmp 24 9 28 1 $glow 1
    Draw-WideLine $bmp 22 9 24 1 $glow 1
    Draw-WideLine $bmp 10 21 21 21 $accent 1
    Draw-WideLine $bmp 12 31 20 44 $accent 1
}

function Paint-SovereignDetails([System.Drawing.Bitmap]$bmp) {
    $eye = [System.Drawing.Color]::FromArgb(255, 255, 244, 182)
    $rune = [System.Drawing.Color]::FromArgb(210, 145, 215, 255)
    $gold = [System.Drawing.Color]::FromArgb(185, 248, 232, 166)
    Draw-BrushPoint $bmp 12 19 1 $eye
    Draw-BrushPoint $bmp 19 19 1 $eye
    Draw-WideLine $bmp 15 4 15 13 $gold 1
    Draw-WideLine $bmp 9 7 21 7 $gold 1
    Draw-WideLine $bmp 15 24 15 42 $rune 1
    Draw-WideLine $bmp 10 29 20 29 $rune 1
    Draw-WideLine $bmp 8 49 22 58 $rune 1
}

function Paint-LanternkeeperDetails([System.Drawing.Bitmap]$bmp) {
    $warm = [System.Drawing.Color]::FromArgb(230, 255, 213, 132)
    $cold = [System.Drawing.Color]::FromArgb(170, 119, 170, 197)
    Draw-BrushPoint $bmp 12 19 1 $warm
    Draw-BrushPoint $bmp 19 19 1 $warm
    Draw-WideLine $bmp 10 8 21 8 $cold 1
    Draw-WideLine $bmp 15 23 15 47 $cold 1
    Draw-WideLine $bmp 8 44 22 44 $warm 1
    Draw-RingWide $bmp 15 49 6 $warm 1
}

function Paint-KnightDetails([System.Drawing.Bitmap]$bmp) {
    $steel = [System.Drawing.Color]::FromArgb(185, 214, 232, 246)
    $moon = [System.Drawing.Color]::FromArgb(180, 132, 204, 255)
    Draw-WideLine $bmp 15 4 15 12 $steel 1
    Draw-WideLine $bmp 10 8 20 8 $steel 1
    Draw-WideLine $bmp 15 20 15 48 $moon 1
    Draw-WideLine $bmp 10 28 20 28 $steel 1
    Draw-WideLine $bmp 8 49 22 58 $moon 1
}

function Paint-WardenDetails([System.Drawing.Bitmap]$bmp) {
    $horn = [System.Drawing.Color]::FromArgb(210, 212, 245, 246)
    $frost = [System.Drawing.Color]::FromArgb(170, 143, 224, 226)
    Draw-WideLine $bmp 10 7 7 0 $horn 1
    Draw-WideLine $bmp 21 7 24 0 $horn 1
    Draw-BrushPoint $bmp 12 19 1 $horn
    Draw-BrushPoint $bmp 19 19 1 $horn
    Draw-WideLine $bmp 15 25 15 47 $frost 1
    Draw-WideLine $bmp 10 31 20 31 $frost 1
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
Create-FrameTop (Join-Path $blockDir "moonwoven_frame_top.png")
Create-FrameSide (Join-Path $blockDir "moonwoven_frame_side.png")
Create-FrameBottom (Join-Path $blockDir "moonwoven_frame_bottom.png")
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
Create-ArmorItem (Join-Path $itemDir "mirror_helmet.png") "helmet"
Create-ArmorItem (Join-Path $itemDir "mirror_chestplate.png") "chestplate"
Create-ArmorItem (Join-Path $itemDir "mirror_leggings.png") "leggings"
Create-ArmorItem (Join-Path $itemDir "mirror_boots.png") "boots"
Create-SovereignArmorItem (Join-Path $itemDir "sovereign_helmet.png") "helmet"
Create-SovereignArmorItem (Join-Path $itemDir "sovereign_chestplate.png") "chestplate"
Create-SovereignArmorItem (Join-Path $itemDir "sovereign_leggings.png") "leggings"
Create-SovereignArmorItem (Join-Path $itemDir "sovereign_boots.png") "boots"
Create-ToolItem (Join-Path $itemDir "sovereign_glaive.png") "glaive"
Create-CrystalItem (Join-Path $itemDir "starfragment.png") "#f2d56e" "#fff9d3"
Create-SanctumKeyItem (Join-Path $itemDir "sanctum_key.png")
Create-CathedralSealItem (Join-Path $itemDir "cathedral_seal.png")
Create-WardenSigilItem (Join-Path $itemDir "warden_sigil.png")
Create-CharmItem (Join-Path $itemDir "hush_ward_charm.png")
Create-SovereignWardItem (Join-Path $itemDir "sovereign_ward.png")
Create-AstralSigilItem (Join-Path $itemDir "astral_sigil.png")
Create-CrystalItem (Join-Path $itemDir "gloamwood_sap.png") "#5ba8bb" "#e1fcff"
Create-SovereignCoreItem (Join-Path $itemDir "sovereign_core.png")
Create-SpawnEggItem (Join-Path $itemDir "mirror_stag_spawn_egg.png") "#d3ece6" "#6ca4aa"
Create-SpawnEggItem (Join-Path $itemDir "hushling_spawn_egg.png") "#2d444a" "#a9efe4"
Create-SpawnEggItem (Join-Path $itemDir "hush_sovereign_spawn_egg.png") "#0c1119" "#9ad9ff"
Create-SpawnEggItem (Join-Path $itemDir "lanternkeeper_spawn_egg.png") "#20303c" "#ffd78c"
Create-SpawnEggItem (Join-Path $itemDir "moonfallen_knight_spawn_egg.png") "#2b2f3b" "#dbe7ef"
Create-SpawnEggItem (Join-Path $itemDir "veilbound_watcher_spawn_egg.png") "#111725" "#91c8f1"
Create-SpawnEggItem (Join-Path $itemDir "warden_of_silence_spawn_egg.png") "#313941" "#d9edf3"

$mirrorStag = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/cow/temperate_cow.png"
Recolor-Entity $mirrorStag (Color-Hex "#1a2025") (Color-Hex "#5b7f86") (Color-Hex "#dff5ee") (Color-Hex "#e7d18e")
Paint-StagDetails $mirrorStag
Save-Png $mirrorStag (Join-Path $entityDir "mirror_stag.png")

$hushling = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/zombie/zombie.png"
Recolor-Entity $hushling (Color-Hex "#13191d") (Color-Hex "#38646c") (Color-Hex "#9fe3da") (Color-Hex "#d5fff7")
Paint-HushlingDetails $hushling
Save-Png $hushling (Join-Path $entityDir "hushling.png")

$hushSovereign = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/skeleton/wither_skeleton.png"
Recolor-Entity $hushSovereign (Color-Hex "#05070c") (Color-Hex "#3d4c61") (Color-Hex "#b7d9f5") (Color-Hex "#fff1b0")
Paint-SovereignDetails $hushSovereign
Save-Png $hushSovereign (Join-Path $entityDir "hush_sovereign.png")

$lanternkeeper = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/illager/pillager.png"
Recolor-Entity $lanternkeeper (Color-Hex "#12161c") (Color-Hex "#566a76") (Color-Hex "#d8e5ea") (Color-Hex "#ffd88d")
Paint-LanternkeeperDetails $lanternkeeper
Save-Png $lanternkeeper (Join-Path $entityDir "lanternkeeper.png")

$moonfallenKnight = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/skeleton/wither_skeleton.png"
Recolor-Entity $moonfallenKnight (Color-Hex "#0d1117") (Color-Hex "#556271") (Color-Hex "#dce7ef") (Color-Hex "#9dd4ff")
Paint-KnightDetails $moonfallenKnight
Save-Png $moonfallenKnight (Join-Path $entityDir "moonfallen_knight.png")

$watcher = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/enderman/enderman.png"
Recolor-Entity $watcher (Color-Hex "#06070d") (Color-Hex "#24334b") (Color-Hex "#9dc5e8") (Color-Hex "#d0f6ff")
Paint-WatcherDetails $watcher
Save-Png $watcher (Join-Path $entityDir "veilbound_watcher.png")

$warden = Load-ZipBitmap $minecraftZipPath "assets/minecraft/textures/entity/skeleton/wither_skeleton.png"
Recolor-Entity $warden (Color-Hex "#0a0e12") (Color-Hex "#4c5965") (Color-Hex "#d8e7ef") (Color-Hex "#b4edf0")
Paint-WardenDetails $warden
Save-Png $warden (Join-Path $entityDir "warden_of_silence.png")

$humanoidDir = Join-Path $entityDir "equipment\humanoid"
$leggingsDir = Join-Path $entityDir "equipment\humanoid_leggings"
foreach ($dir in @($humanoidDir, $leggingsDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}
Create-ArmorLayer (Join-Path $humanoidDir "mirrorsteel.png") $false
Create-ArmorLayer (Join-Path $leggingsDir "mirrorsteel.png") $true
Create-SovereignArmorLayer (Join-Path $humanoidDir "sovereign.png") $false
Create-SovereignArmorLayer (Join-Path $leggingsDir "sovereign.png") $true

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
