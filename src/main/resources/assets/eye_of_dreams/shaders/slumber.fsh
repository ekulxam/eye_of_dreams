#version 150

uniform sampler2D InSampler;

layout(std140) uniform ColorChange {
    vec4 MulColor;
    vec4 AddColor;
};

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main(){
    // modified from sobel.fsh from the original super secret settings
    vec4 center = texture(InSampler, texCoord);
    if (center.r <= 0.28 && center.g >= 0.65 && center.b >= 0.65) {
        fragColor = vec4(center.rgb, 1.0);
        return;
    }
    vec4 left = texture(InSampler, texCoord - vec2(oneTexel.x, 0.0));
    vec4 right = texture(InSampler, texCoord + vec2(oneTexel.x, 0.0));
    vec4 up = texture(InSampler, texCoord - vec2(0.0, oneTexel.y));
    vec4 down = texture(InSampler, texCoord + vec2(0.0, oneTexel.y));
    vec4 leftDiff  = center - left;
    vec4 rightDiff = center - right;
    vec4 upDiff    = center - up;
    vec4 downDiff  = center - down;
    vec4 total = clamp((leftDiff + rightDiff + upDiff + downDiff) * AddColor, 0.0, 1.0);
    if (total.r > 0.1 || total.g > 0.1 || total.b > 0.1) total = clamp(total + AddColor, 0.0, 1.0);
    //if (total.r > 0.25) {
    //    if (total.b < total.r) total.b = clamp(total.b + 0.5, 0.0, 1.0);
    //    if (total.g < total.r) total.g = clamp(total.g + 0.2, 0.0, 1.0);
    //}
    total.g = mix(total.g, total.b, abs(total.b - 0.5) + 0.5);
    fragColor = vec4(total.rgb, 1.0);
}
