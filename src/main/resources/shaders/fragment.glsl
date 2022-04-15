#version 330 core

in vec4 fColor;
in vec2 fTextCoords;
in float fTextId;

uniform sampler2D textures[8];

out vec4 color;

void main()
{
    if (fTextId > 0) {
        int id = int(fTextId);
        color = fColor * texture(textures[id], fTextCoords);
        //color = vec4(fTextCoords, 0, 1);
    } else {
        color = fColor;
    }
}