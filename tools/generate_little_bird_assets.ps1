Add-Type -AssemblyName System.Drawing

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$assetsRoot = Join-Path $root "src\main\resources\assets\veilreach"
$itemDir = Join-Path $assetsRoot "textures\item"
$entityDir = Join-Path $assetsRoot "textures\entity"
$blockDir = Join-Path $assetsRoot "textures\block"

foreach ($dir in @($itemDir, $entityDir, $blockDir)) {
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

function Create-RiftSkimmerItem([string]$path) {
    $bmp = New-Bitmap 32 32
    $body = Color-Hex "#2d4458"
    $glow = Color-Hex "#96efff"
    $fin = Color-Hex "#8c70ff"
    Fill-Rect $bmp 7 13 18 5 $body
    Fill-Rect $bmp 11 10 10 3 $glow
    Fill-Rect $bmp 6 14 3 4 $fin
    Fill-Rect $bmp 23 14 3 4 $fin
    Draw-Line $bmp 16 6 4 10 $glow
    Draw-Line $bmp 16 6 28 10 $glow
    Draw-Line $bmp 16 18 22 25 $fin
    Save-Png $bmp $path
}

function Create-RiftGunshipItem([string]$path) {
    $bmp = New-Bitmap 32 32
    $body = Color-Hex "#31495d"
    $glass = Color-Hex "#9fe7f7"
    $accent = Color-Hex "#d6be86"
    $flare = Color-Hex "#89d6ff"
    Fill-Rect $bmp 7 12 18 7 $body
    Fill-Rect $bmp 10 8 12 4 $glass
    Fill-Rect $bmp 4 13 4 5 $body
    Fill-Rect $bmp 24 13 4 5 $body
    Fill-Rect $bmp 11 19 10 2 $accent
    Draw-Line $bmp 16 6 5 3 $flare
    Draw-Line $bmp 16 6 27 3 $flare
    Draw-Line $bmp 16 19 16 26 $accent
    Save-Png $bmp $path
}

function Create-RiftSiegeItem([string]$path) {
    $bmp = New-Bitmap 32 32
    $body = Color-Hex "#4e515f"
    $plate = Color-Hex "#d3b58b"
    $core = Color-Hex "#b488ff"
    Fill-Rect $bmp 6 13 20 7 $body
    Fill-Rect $bmp 11 9 10 4 $plate
    Fill-Rect $bmp 14 6 4 7 $core
    Fill-Rect $bmp 4 15 3 5 $body
    Fill-Rect $bmp 25 15 3 5 $body
    Draw-Line $bmp 16 9 16 3 $plate
    Save-Png $bmp $path
}

function Create-PulseBoltItem([string]$path) {
    $bmp = New-Bitmap 32 32
    $glow = Color-Hex "#9af4ff"
    $core = Color-Hex "#4a72b7"
    Draw-Circle $bmp 16 16 6 $glow
    Fill-Rect $bmp 14 8 4 16 $core
    Fill-Rect $bmp 8 14 16 4 $core
    Save-Png $bmp $path
}

function Create-ShellItem([string]$path, [System.Drawing.Color]$body, [System.Drawing.Color]$accent) {
    $bmp = New-Bitmap 32 32
    Fill-Rect $bmp 13 5 6 16 $body
    Fill-Rect $bmp 12 3 8 3 $accent
    Fill-Rect $bmp 11 19 10 5 $body
    Draw-Line $bmp 11 21 8 26 $accent
    Draw-Line $bmp 21 21 24 26 $accent
    Save-Png $bmp $path
}

function Create-RiftSkimmerEntityTexture([string]$path) {
    $bmp = New-Bitmap 64 32
    $body = Color-Hex "#2d4355"
    $panel = Color-Hex "#4f6375"
    $glow = Color-Hex "#9cecff"
    $fin = Color-Hex "#8f72ff"
    Fill-Rect $bmp 0 0 24 12 $body
    Fill-Rect $bmp 24 0 20 12 $panel
    Fill-Rect $bmp 44 0 20 12 $glow
    Fill-Rect $bmp 0 14 20 10 $body
    Fill-Rect $bmp 20 14 20 10 $glow
    Fill-Rect $bmp 40 14 24 10 $fin
    Draw-Line $bmp 4 6 20 6 $glow
    Draw-Line $bmp 22 6 38 6 $fin
    Draw-Line $bmp 4 20 56 20 $glow
    Save-Png $bmp $path
}

function Create-RiftGunshipEntityTexture([string]$path) {
    $bmp = New-Bitmap 128 96
    $body = Color-Hex "#32485a"
    $panel = Color-Hex "#4f6678"
    $glass = Color-Hex "#9fe5f3"
    $accent = Color-Hex "#d6be87"
    $glow = Color-Hex "#90d9ff"
    $shadow = Color-Hex "#1d2831"

    Fill-Rect $bmp 0 0 128 96 $shadow
    Fill-Rect $bmp 6 8 42 18 $body
    Fill-Rect $bmp 50 8 24 16 $glass
    Fill-Rect $bmp 76 8 28 16 $panel
    Fill-Rect $bmp 6 30 40 14 $panel
    Fill-Rect $bmp 50 30 24 14 $body
    Fill-Rect $bmp 78 30 26 14 $panel
    Fill-Rect $bmp 8 48 32 10 $body
    Fill-Rect $bmp 42 48 28 10 $glow
    Fill-Rect $bmp 72 48 34 10 $panel
    Fill-Rect $bmp 8 64 26 6 $accent
    Fill-Rect $bmp 36 64 22 6 $body
    Fill-Rect $bmp 60 64 24 6 $accent
    Fill-Rect $bmp 86 64 20 6 $panel
    Fill-Rect $bmp 90 18 28 8 $accent
    Fill-Rect $bmp 92 32 26 8 $glow
    Fill-Rect $bmp 92 44 20 8 $panel
    Draw-Line $bmp 12 16 42 16 $accent
    Draw-Line $bmp 54 16 72 16 $accent
    Draw-Line $bmp 78 16 100 16 $accent
    Draw-Line $bmp 10 36 44 36 $glow
    Draw-Line $bmp 54 36 102 36 $accent
    Draw-Line $bmp 10 54 102 54 $glow
    Draw-Line $bmp 12 67 104 67 $accent
    Save-Png $bmp $path
}

function Create-RiftSiegeEntityTexture([string]$path) {
    $bmp = New-Bitmap 80 48
    $body = Color-Hex "#494c5a"
    $plate = Color-Hex "#d2b288"
    $core = Color-Hex "#b487ff"
    $shadow = Color-Hex "#272b36"
    Fill-Rect $bmp 0 0 32 16 $body
    Fill-Rect $bmp 32 0 24 16 $plate
    Fill-Rect $bmp 56 0 24 16 $shadow
    Fill-Rect $bmp 0 18 28 12 $body
    Fill-Rect $bmp 28 18 24 12 $core
    Fill-Rect $bmp 52 18 28 12 $plate
    Fill-Rect $bmp 0 34 80 8 $shadow
    Draw-Line $bmp 6 8 26 8 $plate
    Draw-Line $bmp 34 8 48 8 $core
    Draw-Line $bmp 8 24 72 24 $plate
    Save-Png $bmp $path
}

function Create-ServiceBlock([string]$path, [System.Drawing.Color]$base, [System.Drawing.Color]$accent, [System.Drawing.Color]$trim) {
    $bmp = New-Bitmap 16 16
    Fill-Rect $bmp 0 0 16 16 $base
    Fill-Rect $bmp 2 2 12 12 $trim
    Fill-Rect $bmp 4 4 8 8 $accent
    Draw-Line $bmp 0 8 15 8 $accent
    Draw-Line $bmp 8 0 8 15 $accent
    Save-Png $bmp $path
}

Create-LittleBirdItem (Join-Path $itemDir "little_bird.png")
Create-RocketItem (Join-Path $itemDir "little_bird_rocket.png")
Create-BombItem (Join-Path $itemDir "little_bird_bomb.png") $false
Create-BombItem (Join-Path $itemDir "little_bird_bomblet.png") $true
Create-LittleBirdEntityTexture (Join-Path $entityDir "little_bird.png")
Create-RocketEntityTexture (Join-Path $entityDir "little_bird_rocket.png")
Create-RiftSkimmerItem (Join-Path $itemDir "rift_skimmer.png")
Create-RiftGunshipItem (Join-Path $itemDir "rift_gunship.png")
Create-RiftSiegeItem (Join-Path $itemDir "rift_siege_platform.png")
Create-PulseBoltItem (Join-Path $itemDir "rift_pulse_bolt.png")
Create-ShellItem (Join-Path $itemDir "rift_gunship_rocket.png") (Color-Hex "#5f6f80") (Color-Hex "#d4ba83")
Create-ShellItem (Join-Path $itemDir "rift_gunship_round.png") (Color-Hex "#7b8794") (Color-Hex "#9fe7ff")
Create-ShellItem (Join-Path $itemDir "rift_siege_shell.png") (Color-Hex "#5a6070") (Color-Hex "#d8c39f")
Create-ShellItem (Join-Path $itemDir "rift_suppressor_charge.png") (Color-Hex "#6f4db3") (Color-Hex "#c8a1ff")
Create-RiftSkimmerEntityTexture (Join-Path $entityDir "rift_skimmer.png")
Create-RiftGunshipEntityTexture (Join-Path $entityDir "rift_gunship.png")
Create-RiftSiegeEntityTexture (Join-Path $entityDir "rift_siege_platform.png")
Create-ServiceBlock (Join-Path $blockDir "vehicle_repair_pad.png") (Color-Hex "#1c3842") (Color-Hex "#82f0bc") (Color-Hex "#2b5f66")
Create-ServiceBlock (Join-Path $blockDir "vehicle_rearm_node.png") (Color-Hex "#2b2348") (Color-Hex "#c59aff") (Color-Hex "#4b3f77")
Create-ServiceBlock (Join-Path $blockDir "vehicle_recall_beacon.png") (Color-Hex "#233042") (Color-Hex "#8fdcff") (Color-Hex "#3e5872")
Create-ServiceBlock (Join-Path $blockDir "vehicle_deployment_pad.png") (Color-Hex "#20333a") (Color-Hex "#e5d596") (Color-Hex "#48626a")
