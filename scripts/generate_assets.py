import math, os
from PIL import Image, ImageDraw, ImageFilter

def render_logo(size=1024, scale_artwork=1.0, bg_color='#1B1D23', transparent_bg=False):
    ss = 4
    canvas_w = size * ss
    canvas_h = size * ss
    
    if transparent_bg:
        img = Image.new('RGBA', (canvas_w, canvas_h), (0, 0, 0, 0))
    else:
        h = bg_color.lstrip('#')
        rgb = tuple(int(h[i:i+2], 16) for i in (0, 2, 4))
        img = Image.new('RGBA', (canvas_w, canvas_h), rgb + (255,))
        
    s = (size / 1024.0) * ss
    
    bars = [
        (512, 423, 565, 103, 15.5, '#2AC1AE'),
        (423, 624, 381, 103, 15.5, '#F5943B')
    ]
    
    for cx, cy, L, T, angle_ccw, col_hex in bars:
        h = col_hex.lstrip('#')
        col_rgba = tuple(int(h[i:i+2], 16) for i in (0, 2, 4)) + (255,)
        
        scaled_cx = 512 + (cx - 512) * scale_artwork
        scaled_cy = 512 + (cy - 512) * scale_artwork
        scaled_L = L * scale_artwork
        scaled_T = T * scale_artwork
        
        pw = int(scaled_L * s)
        ph = int(scaled_T * s)
        pr = ph / 2.0
        
        pill = Image.new('RGBA', (pw, ph), (0, 0, 0, 0))
        pdraw = ImageDraw.Draw(pill)
        pdraw.rounded_rectangle([0, 0, pw - 1, ph - 1], radius=pr, fill=col_rgba)
        
        rotated = pill.rotate(angle_ccw, resample=Image.BICUBIC, expand=True)
        rw, rh = rotated.size
        
        px = int(scaled_cx * s - rw / 2.0)
        py = int(scaled_cy * s - rh / 2.0)
        img.paste(rotated, (px, py), rotated)
        
    final_img = img.resize((size, size), Image.LANCZOS)
    return final_img

# 1. Verification with Logo-ref.jpeg
rendered_1024 = render_logo(1024, 1.0, '#1B1D23', False).convert('RGB')
ref_path = 'design-refs/Logo-ref.jpeg' if os.path.exists('design-refs/Logo-ref.jpeg') else 'Design Assets/Logo-ref.jpeg'
ref = Image.open(ref_path).convert('RGB')

def analyze_bars(im):
    teal_pts = []
    orange_pts = []
    for y in range(1024):
        for x in range(1024):
            if x > 850 and y > 850:
                continue
            r, g, b = im.getpixel((x, y))
            if g > 150 and b > 120 and r < 100:
                teal_pts.append((x, y))
            elif r > 180 and g > 100 and b < 100:
                orange_pts.append((x, y))
    t_min_x = min(p[0] for p in teal_pts); t_max_x = max(p[0] for p in teal_pts)
    t_min_y = min(p[1] for p in teal_pts); t_max_y = max(p[1] for p in teal_pts)
    o_min_x = min(p[0] for p in orange_pts); o_max_x = max(p[0] for p in orange_pts)
    o_min_y = min(p[1] for p in orange_pts); o_max_y = max(p[1] for p in orange_pts)
    return {
        'teal_bbox': (t_min_x, t_max_x, t_min_y, t_max_y),
        'teal_cx': sum(p[0] for p in teal_pts) / len(teal_pts),
        'teal_cy': sum(p[1] for p in teal_pts) / len(teal_pts),
        'orange_bbox': (o_min_x, o_max_x, o_min_y, o_max_y),
        'orange_cx': sum(p[0] for p in orange_pts) / len(orange_pts),
        'orange_cy': sum(p[1] for p in orange_pts) / len(orange_pts),
    }

stat_ref = analyze_bars(ref)
stat_ren = analyze_bars(rendered_1024)

print('=== LOGO GEOMETRY VERIFICATION ===')
print('Ref Teal BBox:    x [%d, %d], y [%d, %d]' % stat_ref['teal_bbox'])
print('Rendered Teal:    x [%d, %d], y [%d, %d]' % stat_ren['teal_bbox'])
print('Ref Orange BBox:  x [%d, %d], y [%d, %d]' % stat_ref['orange_bbox'])
print('Rendered Orange:  x [%d, %d], y [%d, %d]' % stat_ren['orange_bbox'])

t_dx = abs(stat_ref['teal_cx'] - stat_ren['teal_cx'])
t_dy = abs(stat_ref['teal_cy'] - stat_ren['teal_cy'])
o_dx = abs(stat_ref['orange_cx'] - stat_ren['orange_cx'])
o_dy = abs(stat_ref['orange_cy'] - stat_ren['orange_cy'])
print('Teal centroid delta:   dx = %.2f px (%.2f%%), dy = %.2f px (%.2f%%)' % (t_dx, t_dx / 1024 * 100, t_dy, t_dy / 1024 * 100))
print('Orange centroid delta: dx = %.2f px (%.2f%%), dy = %.2f px (%.2f%%)' % (o_dx, o_dx / 1024 * 100, o_dy, o_dy / 1024 * 100))

# 2. Render Play Store 512x512 icon
playstore_512 = render_logo(512, 1.0, '#1B1D23', False)
playstore_path = 'design-refs/logo/spendsense-playstore-512.png'
playstore_512.save(playstore_path, 'PNG')
print('Saved Play Store icon:', playstore_path)

# 3. Render Previews
os.makedirs('design-refs/previews', exist_ok=True)

# 48x48 px
p48 = render_logo(48, 1.0, '#1B1D23', False)
p48.save('design-refs/previews/preview-48x48.png', 'PNG')

# Master icon with 0.93 scale for adaptive launcher
icon_size = 512
adaptive_scaled = render_logo(icon_size, 0.93, '#1B1D23', False)

# Mask shapes:
# Helper to apply mask
def apply_mask(base_icon, mask_func):
    mask = Image.new('L', base_icon.size, 0)
    draw = ImageDraw.Draw(mask)
    mask_func(draw, base_icon.size[0])
    out = Image.new('RGBA', base_icon.size, (0, 0, 0, 0))
    out.paste(base_icon, (0, 0), mask)
    return out

# Circle: radius = size * (66/108) / 2 = size * 0.3055
def draw_circle_mask(draw, sz):
    # Adaptive icon visible area is usually ~72dp of 108dp (diameter ~72/108 * sz)
    pad = int(sz * (1 - 72.0/108.0) / 2.0)
    draw.ellipse([pad, pad, sz - 1 - pad, sz - 1 - pad], fill=255)

# Rounded Square: corner radius ~18dp of 72dp
def draw_rounded_square_mask(draw, sz):
    pad = int(sz * (1 - 72.0/108.0) / 2.0)
    r = int(sz * (18.0 / 108.0))
    draw.rounded_rectangle([pad, pad, sz - 1 - pad, sz - 1 - pad], radius=r, fill=255)

# Squircle (superellipse with n ~ 4)
def draw_squircle_mask(draw, sz):
    pad = int(sz * (1 - 72.0/108.0) / 2.0)
    r = int(sz * (30.0 / 108.0)) # smooth squircle approximation
    draw.rounded_rectangle([pad, pad, sz - 1 - pad, sz - 1 - pad], radius=r, fill=255)

apply_mask(adaptive_scaled, draw_circle_mask).save('design-refs/previews/preview-circle.png', 'PNG')
apply_mask(adaptive_scaled, draw_squircle_mask).save('design-refs/previews/preview-squircle.png', 'PNG')
apply_mask(adaptive_scaled, draw_rounded_square_mask).save('design-refs/previews/preview-rounded-square.png', 'PNG')

# Wallpaper previews:
# Show the masked circle icon on a wallpaper background
def make_wallpaper_preview(wp_color, is_dark):
    wp = Image.new('RGB', (800, 600), wp_color)
    wp_draw = ImageDraw.Draw(wp)
    # Add subtle gradient or card
    icon = apply_mask(adaptive_scaled, draw_circle_mask).resize((144, 144), Image.LANCZOS)
    
    # Draw soft shadow under icon
    shadow = Image.new('RGBA', (154, 154), (0, 0, 0, 0))
    s_draw = ImageDraw.Draw(shadow)
    s_draw.ellipse([5, 5, 149, 149], fill=(0, 0, 0, 60 if not is_dark else 120))
    shadow = shadow.filter(ImageFilter.GaussianBlur(radius=4))
    
    wp.paste(shadow, (325, 203), shadow)
    wp.paste(icon, (328, 200), icon)
    return wp

make_wallpaper_preview('#F0F2F5', False).save('design-refs/previews/preview-wallpaper-light.png', 'PNG')
make_wallpaper_preview('#121316', True).save('design-refs/previews/preview-wallpaper-dark.png', 'PNG')

print('All previews generated successfully in design-refs/previews/')
