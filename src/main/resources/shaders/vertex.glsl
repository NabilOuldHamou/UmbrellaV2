#version 330 core
layout (location=0) in vec3 position;
layout (location=1) in vec4 color;
layout (location=2) in vec2 textCoords;
layout (location=3) in float textId;

uniform mat4 projection;
uniform mat4 view;

out vec4 fColor;
out vec2 fTextCoords;
out float fTextId;

void main()
{
    fColor = color;
    fTextCoords = textCoords;
    fTextId = textId;

    gl_Position = projection * view * vec4(position, 1.0);
}