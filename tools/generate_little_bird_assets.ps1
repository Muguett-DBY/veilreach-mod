Add-Type -AssemblyName System.Drawing

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$assetsRoot = Join-Path $root "src\main\resources\assets\veilreach"
$itemDir = Join-Path $assetsRoot "textures\item"
$entityDir = Join-Path $assetsRoot "textures\entity"

foreach ($dir in @($itemDir, $entityDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

function Color-Hex([string]$hex) {
    return [System.Drawing.ColorTranslator]::FromHtml($hex)
}

function New-Bitmap([int]$width, [int]$height) {
    $bmp = [System.Drawing.Bitmap]::new($width, $height)
    for ($x = 0; $x -lt $width; $x++) {
        for ($y = 0; $y -lt $height; $y++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }
    return $bmp
}

function Save-Png([System.Drawing.Bitmap]$bmp, [string]$path) {
    $tmp = "$path.tmp"
    if (Test-Path $tmp) {
        Remove-Item $tmp -Force
    }
    $bmp.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png)
    Move-Item -Force $tmp $path
    $bmp.Dispose()
}

function Fill-Rect([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [int]$w, [int]$h, [System.Drawing.Color]$color) {
    for ($px = $x; $px -lt ($x + $w); $px++) {
        for ($py = $y; $py -lt ($y + $h); $py++) {
            if ($px -ge 0 -and $px -lt $bmp.Width -and $py -ge 0 -and $py -lt $bmp.Height) {
                $bmp.SetPixel($px, $py, $color)
            }
        }
    }
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

function Draw-Circle([System.Drawing.Bitmap]$bmp, [int]$cx, [int]$cy, [int]$radius, [System.Drawing.Color]$color) {
    for ($x = -$radius; $x -le $radius; $x++) {
        for ($y = -$radius; $y -le $radius; $y++) {
            if (($x * $x) + ($y * $y) -le ($radius * $radius)) {
                $px = $cx + $x
                $py = $cy + $y
                if ($px -ge 0 -and $px -lt $bmp.Width -and $py -ge 0 -and $py -lt $bmp.Height) {
                    $bmp.SetPixel($px, $py, $color)
                }
            }
        }
    }
}

function Create-LittleBirdItem([string]$path) {
    $bmp = New-Bitmap 32 32
    $dark = Color-Hex "#243544"
    $mid = Color-Hex "#506b7c"
    $glass = Color-Hex "#a5dff4"
    $gold = Color-Hex "#f2d68f"
    Fill-Rect $bmp 7 12 18 6 $dark
    Fill-Rect $bmp 10 9 10 4 $mid
    Fill-Rect $bmp 9 13 5 3 $glass
    Fill-Rect $bmp 18 13 4 3 $glass
    Fill-Rect $bmp 22 13 5 2 $dark
    Fill-Rect $bmp 5 18 22 2 $mid
    Fill-Rect $bmp 5 20 2 4 $mid
    Fill-Rect $bmp 25 20 2 4 $mid
    Draw-Line $bmp 16 4 16 11 $gold
    Draw-Line $bmp 4 6 28 6 $gold
    Draw-Line $bmp 16 6 26 2 $gold
    Draw-Line $bmp 16 6 6 2 $gold
    Draw-Line $bmp 24 13 29 11 $mid
    Save-Png $bmp $path
}

function Create-RocketItem([string]$path) {
    $bmp = New-Bitmap 32 32
    $shell = Color-Hex "#c9d7e3"
    $fin = Color-Hex "#f28a54"
    $core = Color-Hex "#425364"
    Fill-Rect $bmp 14 5 4 16 $shell
    Fill-Rect $bmp 13 3 6 3 $fin
    Fill-Rect $bmp 12 19 8 4 $core
    Draw-Line $bmp 12 21 9 26 $fin
    Draw-Line $bmp 20 21 23 26 $fin
    Draw-Line $bmp 16 21 16 28 $fin
    Save-Png $bmp $path
}

function Create-RocketEntityTexture([string]$path) {
    $bmp = New-Bitmap 32 32
    $shell = Color-Hex "#cdd9e6"
    $nose = Color-Hex "#f29c59"
    $stripe = Color-Hex "#6f8598"
    $fin = Color-Hex "#e16843"
    $shadow = Color-Hex "#2d3944"
    $glow = Color-Hex "#f6d386"

    Fill-Rect $bmp 0 0 32 32 $shadow
    Fill-Rect $bmp 0 0 12 6 $shell
    Fill-Rect $bmp 12 0 6 6 $nose
    Fill-Rect $bmp 18 0 8 6 $shell
    Fill-Rect $bmp 26 0 6 6 $stripe

    Fill-Rect $bmp 0 8 8 8 $shell
    Fill-Rect $bmp 8 8 8 8 $stripe
    Fill-Rect $bmp 16 8 8 8 $shell
    Fill-Rect $bmp 24 8 8 8 $glow

    Fill-Rect $bmp 0 18 6 6 $fin
    Fill-Rect $bmp 6 18 6 6 $shell
    Fill-Rect $bmp 12 18 6 6 $fin
    Fill-Rect $bmp 18 18 6 6 $shell
    Fill-Rect $bmp 24 18 8 6 $fin

    Draw-Line $bmp 2 3 10 3 $glow
    Draw-Line $bmp 20 3 28 3 $glow
    Draw-Line $bmp 2 12 28 12 $glow
    Draw-Line $bmp 4 20 28 20 $glow

    Save-Png $bmp $path
}

function Create-BombItem([string]$path, [bool]$small) {
    $bmp = New-Bitmap 32 32
    $dark = if ($small) { Color-Hex "#49545e" } else { Color-Hex "#313e4b" }
    $accent = if ($small) { Color-Hex "#d7b46d" } else { Color-Hex "#efcd77" }
    $size = if ($small) { 5 } else { 7 }
    Draw-Circle $bmp 16 15 $size $dark
    Fill-Rect $bmp 14 6 4 4 $accent
    Draw-Line $bmp 16 7 20 4 $accent
    Draw-Line $bmp 10 15 22 15 $accent
    Draw-Line $bmp 16 9 16 21 $accent
    if (-not $small) {
        Draw-Line $bmp 11 10 21 20 $accent
        Draw-Line $bmp 21 10 11 20 $accent
    }
    Save-Png $bmp $path
}

function Create-LittleBirdEntityTexture([string]$path) {
    $bmp = New-Bitmap 128 96
    $body = Color-Hex "#334556"
    $panel = Color-Hex "#52697b"
    $glass = Color-Hex "#9ddcf0"
    $gold = Color-Hex "#efd58c"
    $warning = Color-Hex "#cf5f47"
    $shadow = Color-Hex "#1b2630"

    Fill-Rect $bmp 0 0 128 96 $shadow

    Fill-Rect $bmp 6 8 36 18 $body
    Fill-Rect $bmp 44 8 24 16 $glass
    Fill-Rect $bmp 72 8 28 16 $panel
    Fill-Rect $bmp 6 30 42 14 $panel
    Fill-Rect $bmp 52 30 20 12 $body
    Fill-Rect $bmp 76 30 28 10 $panel

    Fill-Rect $bmp 8 48 12 10 $panel
    Fill-Rect $bmp 24 48 12 10 $panel
    Fill-Rect $bmp 40 48 18 10 $body
    Fill-Rect $bmp 64 48 30 10 $panel

    Fill-Rect $bmp 8 64 40 4 $gold
    Fill-Rect $bmp 56 64 30 4 $gold

    Fill-Rect $bmp 6 74 40 6 $panel
    Fill-Rect $bmp 52 74 10 6 $body
    Fill-Rect $bmp 66 74 18 6 $panel

    Fill-Rect $bmp 92 30 28 6 $warning
    Fill-Rect $bmp 92 40 24 4 $gold
    Fill-Rect $bmp 92 48 20 4 $glass

    Draw-Line $bmp 10 16 36 16 $gold
    Draw-Line $bmp 52 14 66 14 $gold
    Draw-Line $bmp 78 18 98 18 $gold
    Draw-Line $bmp 10 36 44 36 $gold
    Draw-Line $bmp 64 36 98 36 $gold
    Draw-Line $bmp 10 54 34 54 $gold
    Draw-Line $bmp 66 54 90 54 $gold

    Draw-Line $bmp 112 8 124 8 $gold
    Draw-Line $bmp 118 2 118 18 $gold
    Draw-Line $bmp 108 68 124 68 $warning
    Draw-Line $bmp 108 76 124 76 $warning

    Save-Png $bmp $path
}

Create-LittleBirdItem (Join-Path $itemDir "little_bird.png")
Create-RocketItem (Join-Path $itemDir "little_bird_rocket.png")
Create-BombItem (Join-Path $itemDir "little_bird_bomb.png") $false
Create-BombItem (Join-Path $itemDir "little_bird_bomblet.png") $true
Create-LittleBirdEntityTexture (Join-Path $entityDir "little_bird.png")
Create-RocketEntityTexture (Join-Path $entityDir "little_bird_rocket.png")
