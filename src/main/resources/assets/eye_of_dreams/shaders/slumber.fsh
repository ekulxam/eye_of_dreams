#version 150

uniform sampler2D DiffuseSampler;

layout(std140) uniform ColorChange {
    vec4 MulColor;
    vec4 AddColor;
    vec2 Center;
    float Progress;
};

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main(){
    // modified from sobel.fsh from the original super secret settings

    // get texture at texCoord
    vec4 center = texture(DiffuseSampler, texCoord);

    if (length(texCoord - Center) > Progress) { // allows oval progress
        fragColor = center;
        return;
    }

    // if color is cyan enough, don't change anything
    if (center.r < 0.25 && center.g > 0.75 && center.b > 0.75) {
        fragColor = vec4(center.rgb, 1.0);
        return;
    }

    // sobel: get outline
    vec4 left   = texture(DiffuseSampler, texCoord - vec2(oneTexel.x, 0.0));
    vec4 right  = texture(DiffuseSampler, texCoord + vec2(oneTexel.x, 0.0));
    vec4 up     = texture(DiffuseSampler, texCoord - vec2(0.0, oneTexel.y));
    vec4 down   = texture(DiffuseSampler, texCoord + vec2(0.0, oneTexel.y));
    vec4 leftDiff  = center - left;
    vec4 rightDiff = center - right;
    vec4 upDiff    = center - up;
    vec4 downDiff  = center - down;
    vec4 total = clamp((leftDiff + rightDiff + upDiff + downDiff) * MulColor, 0.0, 1.0);
    if (total.r > 0.1 || total.g > 0.1 || total.b > 0.1) total = clamp(total + AddColor, 0.0, 1.0);
    total.g = mix(total.g, total.b, abs(total.b - 0.5) + 0.5);
    fragColor = vec4(total.rgb, 1.0);
}
